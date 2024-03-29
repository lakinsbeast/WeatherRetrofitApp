package code.with.me.weatherretrofitapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import code.with.me.weatherretrofitapp.Items.Location
import code.with.me.weatherretrofitapp.Repositories.GeoRepository
import code.with.me.weatherretrofitapp.Repositories.WeatherRepository
import code.with.me.weatherretrofitapp.Services.GeoService
import code.with.me.weatherretrofitapp.Services.WeatherService
import code.with.me.weatherretrofitapp.ViewModels.GeoViewModel
import code.with.me.weatherretrofitapp.ViewModels.GeoViewModelFactory
import code.with.me.weatherretrofitapp.ViewModels.WeatherViewModel
import code.with.me.weatherretrofitapp.ViewModels.WeatherViewModelFactory
import code.with.me.weatherretrofitapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModelWeather: WeatherViewModel
    private lateinit var viewModelGeo: GeoViewModel



    var name: String = ""
    private var updatedInfo: Boolean = false
    private var getdata: String = ""

    private val weatherService = WeatherService.create()
    private val weatherRepository = WeatherRepository(weatherService)
    private val geoString = GeoService.create()
    private val geoRepository = GeoRepository(geoString)

    private var map: GoogleMap? = null
    private var currentLocation: Location? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()

        binding.mainactivity.setOnRefreshListener {
            fetchLocation()
            if (updatedInfo) {
                viewModelWeather.initWeather()
            } else {
//                Toast.makeText(this, "Выбери город через меню", Toast.LENGTH_LONG).show()
            }
            binding.mainactivity.isRefreshing = false

        }

        viewModelWeather = ViewModelProvider(this,
            WeatherViewModelFactory(weatherRepository)
        )[WeatherViewModel::class.java]
        viewModelGeo = ViewModelProvider(this,
            GeoViewModelFactory(geoRepository)
        )[GeoViewModel::class.java]



        viewModelWeather.town.observe(this) {
            binding.apply {
                TempC.isVisible = false
                TextTitle.isVisible = false
                lastUpdateTitle.isVisible = false
                townTitle.text = viewModelWeather.town.value
                if(viewModelWeather.town.value != null) {
                    ToolbarText.text = viewModelWeather.town.value
                }
                viewModelWeather.tempC.observe(this@MainActivity) { temp_c ->
                    TempC.text = "$temp_c°"
                    TempC.isVisible = true
                    if (TempC.isVisible) {
                        updatedInfo = true
                        Log.d("updatedInfo", "after initWeather")
                    }

                }
                viewModelWeather.textTitle.observe(this@MainActivity) { textTitle ->
                    TextTitle.text = textTitle.toString()
                    TextTitle.isVisible = true
                }
                viewModelWeather.lastUpdateTitleData.observe(this@MainActivity) { lastUpdateTitlee ->
                    lastUpdateTitle.text =
                        ("Последнее обновление погоды:\n$lastUpdateTitlee")
                    lastUpdateTitle.isVisible = true
                }
                viewModelWeather.feelsLikeTitle.observe(this@MainActivity) { feelsLikeTitle ->
                    feelslikeTitle.text = ("По ощущениям:$feelsLikeTitle℃")
                    feelslikeTitle.isVisible = true
                }
            }

        }
        getdata = intent.getStringExtra("theTown").toString()
        viewModelWeather.town.value = getdata
        Log.d("MainActivity", viewModelWeather.town.value.toString())
        viewModelWeather.initWeather()
    }
//    private fun dateDefinition() {
//        val date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//        Log.d("hours", date.toString())
//        when (date) {
//            0,1,2,3,4,5,6 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
//            7,8,9,10,11,12 -> binding.mainactivity.setBackgroundResource(R.drawable.day_forest)
//            13,14,15,16,17,18 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
//            19,20,21,22,23 -> binding.mainactivity.setBackgroundResource(R.drawable.night_forest)
//        }
//    }

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
                    val location: android.location.Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Не удалось определить местоположение", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Местоположение определено успешно", Toast.LENGTH_LONG).show()

                        viewModelGeo.latit.value = location.latitude.toString()
                        viewModelGeo.longt.value = location.longitude.toString()
                        viewModelGeo.initGeo()
                        viewModelGeo.town.observe(this) {
                            if(viewModelWeather.town.value != null) {
                                viewModelWeather.town.value = it
                                viewModelWeather.initWeather()
                                Log.d("город", viewModelGeo.town.value.toString())
                            }

                        }
                    }

                }
            } else {
                Toast.makeText(this, "Включите местоположение", Toast.LENGTH_LONG).show()
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
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    private fun checkPermissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val nightMode = menu?.findItem(R.id.moonMode)
        val locationBtn = menu?.findItem(R.id.gpsMenu)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        nightMode?.setOnMenuItemClickListener { //включает темный режим
            when(it.itemId) {
                R.id.moonMode -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            true
        }
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                Log.d("lightdark", "сработала светлая тема")
                binding.apply {
                    mainactivity.setBackgroundColor(Color.WHITE)
                    townTitle.setTextColor(Color.BLACK)
                    TempC.setTextColor(Color.BLACK)
                    feelslikeTitle.setTextColor(Color.BLACK)
                    lastUpdateTitle.setBackgroundColor(Color.BLACK)
                    TextTitle.setTextColor(Color.BLACK)
                }
            } //light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.d("lightdark", "сработала темная тема")
                nightMode?.isVisible = false
                binding.apply {
                    mainactivity.setBackgroundColor(Color.BLACK)
                    townTitle.setTextColor(Color.WHITE)
                    TempC.setTextColor(Color.WHITE)
                    feelslikeTitle.setTextColor(Color.WHITE)
                    lastUpdateTitle.setBackgroundColor(Color.WHITE)
                    lastUpdateTitle.setTextColor(Color.BLACK)
                    TextTitle.setTextColor(Color.WHITE)
                }
            } //dark theme
        }
        val locationActivity = Intent(this, LocationActivity::class.java)
        locationBtn?.intent = locationActivity
        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
    }
}