package code.with.me.weatherretrofitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import code.with.me.weatherretrofitapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    var name: String = ""
    var updatedInfo: Boolean = false
    private var getdata: String = ""
    private val weatherService = WeatherService.create()
    private val weatherRepository = WeatherRepository(weatherService)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainactivity.setOnRefreshListener {
            if (updatedInfo) {
                viewModel.initWeather()
                Log.d("update", "setonrefreshlistener заработал")
            } else {
                Toast.makeText(this, "Выбери город через меню", Toast.LENGTH_LONG).show()
            }
            binding.mainactivity.isRefreshing = false

        }

        viewModel = ViewModelProvider(this,
            WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel::class.java)

        val date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("hours", date.toString())
        when (date) {
            0,1,2,3,4,5,6 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
            7,8,9,10,11,12 -> binding.mainactivity.setBackgroundResource(R.drawable.day_forest)
            13,14,15,16,17,18 -> binding.mainactivity.setBackgroundResource(R.drawable.evening_forest)
            19,20,21,22,23 -> binding.mainactivity.setBackgroundResource(R.drawable.night_forest)
        }

        Log.d("town.value", viewModel.town.value.toString())
        viewModel.town.observe(this) {
            binding.apply {
                TempC.isVisible = false
                TextTitle.isVisible = false
                lastUpdateTitle.isVisible = false
//                ImageTitle.isVisible = false
                townTitle.text = viewModel.town.value


                viewModel.tempC.observe(this@MainActivity) { temp_c ->
                    binding.TempC.text = "$temp_c℃"
                    TempC.isVisible = true
                    if (TempC.isVisible) {
                        updatedInfo = true
                        Log.d("updatedInfo", "after initWeather")
                    }

                }
                viewModel.textTitle.observe(this@MainActivity) { textTitle ->
                    binding.TextTitle.text = textTitle.toString()
                    TextTitle.isVisible = true
                }
                viewModel.lastUpdateTitleData.observe(this@MainActivity) { lastUpdateTitlee ->
                    binding.lastUpdateTitle.text =
                        ("Последнее обновление погоды:\n$lastUpdateTitlee")
                    lastUpdateTitle.isVisible = true
                }
                viewModel.feelsLikeTitle.observe(this@MainActivity) { feelsLikeTitle ->
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
        viewModel.town.value = getdata
        Log.d("MainActivity", viewModel.town.value.toString())
        viewModel.initWeather()


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
}