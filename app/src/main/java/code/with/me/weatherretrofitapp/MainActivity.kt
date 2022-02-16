package code.with.me.weatherretrofitapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import code.with.me.weatherretrofitapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    var SAVED_TEXT = "mysettings"
    var savedTown: String = ""
    var savedTown2: String = ""
    var name: String = ""
    var getdata: String = ""
    var checkTown: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Сохранение города
        prefs = getSharedPreferences(SAVED_TEXT, Context.MODE_PRIVATE)

        //Проверка на ввод нового города, либо загружается старый сохраненый город
        if (prefs.contains(SAVED_TEXT)) {
            getdata = intent.getStringExtra("theTown").toString()
            if(getdata == "null") { name = prefs.getString(SAVED_TEXT, "")!! }
            else { name = getdata }
        }
        else {
            getdata = intent.getStringExtra("theTown").toString()
            name = getdata
        }

        //при первом запуске вызывается строка кода if
        if (name == "null") { binding.townTitle.text = "Выбери город через меню" }
        else {
            val editor = prefs.edit()
            editor.putString(SAVED_TEXT, name).apply()
            editor.putBoolean(checkTown.toString(), false).apply()

            GlobalScope.launch(Dispatchers.IO) { initWeather() }
        }
    }

    //Меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val menuLocation = menu!!.findItem(R.id.locationMenu)
        val switchActivity = Intent(MainActivity@this,LocationActivity::class.java)
        menuLocation.setIntent(switchActivity)
        return super.onCreateOptionsMenu(menu)
    }

    suspend fun initWeather() = coroutineScope {
        launch {
            val service =
                WeatherService.create().getPosts("a84e682c84344905943193456221202", name, "ru")

            service.enqueue(object : Callback<WeatherItem> {
                override fun onResponse(call: Call<WeatherItem>, response: Response<WeatherItem>) {
                    if (response.body() != null) {
                        binding.TempC.isVisible = true
                        binding.TextTitle.isVisible = true
                        binding.lastUpdateTitle.isVisible = true
                        binding.ImageTitle.isVisible = true
                        binding.townTitle.text = name
                        binding.TempC.text = (response.body()!!.cur.temp_c.toString() + "℃")
                        binding.TextTitle.text = response.body()!!.cur.condition.text
                        binding.feelslikeTitle.text = ("По ощущениям:" + "\n" +response.body()!!.cur.feelslike_c.toString())
                        binding.lastUpdateTitle.text =
                            ("Последнее обновление погоды:" + "\n" + response.body()!!.cur.last_updated)
                        val image_url = ("https:" + response.body()!!.cur.condition.icon)
                        Picasso.get().load(image_url).into(binding.ImageTitle)
                    } else {
                        binding.TempC.text = "Не работает :("
                    }
                }


                override fun onFailure(call: Call<WeatherItem>, t: Throwable) {
                    Log.d("fail", "не робит, сори")
                }
            })
        }
    }

}