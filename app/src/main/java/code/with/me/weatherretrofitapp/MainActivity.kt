package code.with.me.weatherretrofitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import code.with.me.weatherretrofitapp.databinding.ActivityMainBinding
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: WeatherViewModel
    var name: String = ""
    var getdata: String = ""
    val weatherService = WeatherService.create()
    val weatherRepository = WeatherRepository(weatherService)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this,
            WeatherViewModelFactory(weatherRepository)).get(WeatherViewModel::class.java)

        viewModel.town.observe(this, Observer { response ->
            binding.apply {
                TempC.isVisible = false
                TextTitle.isVisible = false
                lastUpdateTitle.isVisible = false
                ImageTitle.isVisible = false

                townTitle.text = viewModel.town.value


                viewModel.tempC.observe(this@MainActivity, Observer { temp_c ->
                    binding.TempC.text = temp_c.toString()
                    TempC.isVisible = true
                })
                viewModel.textTitle.observe(this@MainActivity, Observer { textTitle ->
                    binding.TextTitle.text = textTitle.toString()
                    TextTitle.isVisible = true
                })
                viewModel.lastUpdateTitleData.observe(this@MainActivity, Observer { lastUpdateTitlee ->
                    binding.lastUpdateTitle.text = ("Последнее обновление:\n$lastUpdateTitlee")
                    lastUpdateTitle.isVisible = true
                })
                viewModel.feelsLikeTitle.observe(this@MainActivity, Observer { feelsLikeTitle ->
                    binding.feelslikeTitle.text = ("По ощущениям:\n$feelsLikeTitle")
                    feelslikeTitle.isVisible = true
                })
                viewModel.urlImage.observe(this@MainActivity, Observer { urlImage ->
                    binding.ImageTitle.isVisible = true
                    val imageUrl = ("https:$urlImage")
                    Picasso.get().load(imageUrl).into(binding.ImageTitle)
                })
            }

        })
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
        val switchActivity = Intent(MainActivity@ this, LocationActivity::class.java)
        menuLocation.setIntent(switchActivity)
        return super.onCreateOptionsMenu(menu)
    }
}