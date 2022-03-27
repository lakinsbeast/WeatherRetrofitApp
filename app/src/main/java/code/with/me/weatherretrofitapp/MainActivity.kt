package code.with.me.weatherretrofitapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import code.with.me.weatherretrofitapp.Items.Location
import code.with.me.weatherretrofitapp.Services.GeoService
import code.with.me.weatherretrofitapp.Services.WeatherService
import code.with.me.weatherretrofitapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModelWeather: WeatherViewModel
    private lateinit var viewModelGeo: GeoViewModel

    var name: String = ""
    var updatedInfo: Boolean = false
    private var getdata: String = ""

    private val weatherService = WeatherService.create()
    private val weatherRepository = WeatherRepository(weatherService)
    private val geoString = GeoService.create()
    private val geoRepository = GeoRepository(geoString)

    private var map: GoogleMap? = null
    var currentLocation: Location? = null
    var fusedLocationProvider: FusedLocationProviderClient? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Location
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        //

        binding.mainactivity.setOnRefreshListener {
            if (updatedInfo) {
                viewModelWeather.initWeather()
                Log.d("update", "setonrefreshlistener заработал")
            } else {
                Toast.makeText(this, "Выбери город через меню", Toast.LENGTH_LONG).show()
            }
            binding.mainactivity.isRefreshing = false

        }

        viewModelWeather = ViewModelProvider(this,
            WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel::class.java)
        viewModelGeo = ViewModelProvider(this,
            GeoViewModelFactory(geoRepository)).get(GeoViewModel::class.java)


        val date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("hours", date.toString())
        when (date) {
            0,1,2,3,4,5,6 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
            7,8,9,10,11,12 -> binding.mainactivity.setBackgroundResource(R.drawable.day_forest)
            13,14,15,16,17,18 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
            19,20,21,22,23 -> binding.mainactivity.setBackgroundResource(R.drawable.night_forest)
        }

        Log.d("town.value", viewModelWeather.town.value.toString())
        viewModelWeather.town.observe(this) {
            binding.apply {
                TempC.isVisible = false
                TextTitle.isVisible = false
                lastUpdateTitle.isVisible = false
//                ImageTitle.isVisible = false
                townTitle.text = viewModelWeather.town.value


                viewModelWeather.tempC.observe(this@MainActivity) { temp_c ->
                    binding.TempC.text = "$temp_c℃"
                    TempC.isVisible = true
                    if (TempC.isVisible) {
                        updatedInfo = true
                        Log.d("updatedInfo", "after initWeather")
                    }

                }
                viewModelWeather.textTitle.observe(this@MainActivity) { textTitle ->
                    binding.TextTitle.text = textTitle.toString()
                    TextTitle.isVisible = true
                }
                viewModelWeather.lastUpdateTitleData.observe(this@MainActivity) { lastUpdateTitlee ->
                    binding.lastUpdateTitle.text =
                        ("Последнее обновление погоды:\n$lastUpdateTitlee")
                    lastUpdateTitle.isVisible = true
                }
                viewModelWeather.feelsLikeTitle.observe(this@MainActivity) { feelsLikeTitle ->
                    binding.feelslikeTitle.text = ("По ощущениям:\n$feelsLikeTitle℃")
                    feelslikeTitle.isVisible = true
                }
                //выпилил, потому что иконки были маленького размера
//                viewModel.urlImage.observe(this@MainActivity, Observer { urlImage ->
//                    binding.ImageTitle.isVisible = true
//                    val imageUrl = ("https:$urlImage")
//                    Picasso.get().load(imageUrl).into(binding.ImageTitle)
//                })
            }

        }
        getdata = intent.getStringExtra("theTown").toString()
        viewModelWeather.town.value = getdata
        Log.d("MainActivity", viewModelWeather.town.value.toString())
        viewModelWeather.initWeather()


    }
    private fun fetchLocation() {
        if (checkPermissions())
        {
            if(isLocationEnabled())
            {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProvider?.lastLocation?.addOnCompleteListener(this){ task ->
                    val location: android.location.Location? =task.result
                    if (location == null) {
                        Toast.makeText(this, "Null received", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Get success", Toast.LENGTH_LONG).show()

                        viewModelGeo.latit.value = location.latitude.toString()
                        viewModelGeo.longt.value = location.longitude.toString()
                        binding.lat.text = viewModelGeo.latit.value.toString()
                        binding.longt.text = viewModelGeo.longt.value.toString()
                        viewModelGeo.initGeo()
                        viewModelGeo.town.observe(this) {
                            if(viewModelWeather.town.value != null) {
                                viewModelWeather.town.value = it
                                viewModelWeather.initWeather()
                                Log.d("fuck", viewModelGeo.town.value.toString())
                            }

                        }
                    }

                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private  fun isLocationEnabled():Boolean {
        val locationManager: LocationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
        PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show()
                currentLocation
            } else {
                Toast.makeText(this, "Not Granted", Toast.LENGTH_LONG).show()
            }
        }

    }
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }

    //Меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val menuLocation = menu!!.findItem(R.id.locationMenu)
        val switchActivity = Intent(this, LocationActivity::class.java)
        menuLocation.intent = switchActivity
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
}
}