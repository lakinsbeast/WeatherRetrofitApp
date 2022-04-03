package code.with.me.weatherretrofitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import code.with.me.weatherretrofitapp.databinding.ActivityLocationBinding
import java.util.*

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private var town: String = ""
    private var countries = arrayOf("Paris", "New-York", "London", "Bangkok", "Singapore",
        "Dubai", "Phuket", "Rome", "Tokyo", "Istanbul", "Seoul", "Prague", "Mecca", "Miami",
        "Mumbai", "Barcelona", "Moscow", "Budapest", "Melbourne", "Mexico", "Washington", "Nice",
        "Frankfurt", "Warsaw", "Krakow", "Orlando")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Погодное приложение"


        val date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.d("hours", date.toString())
        when (date) {
            0,1,2,3,4,5,6 -> binding.locactivity.setBackgroundResource(R.drawable.evening_forest)
            7,8,9,10,11,12 -> binding.locactivity.setBackgroundResource(R.drawable.day_forest)
            13,14,15,16,17,18 -> binding.locactivity.setBackgroundResource(R.drawable.evening_forest)
            19,20,21,22,23 -> binding.locactivity.setBackgroundResource(R.drawable.night_forest)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, countries)
        binding.choiceTown.setAdapter(adapter)



        binding.initTown.setOnClickListener {
//            town = binding.editTown.text.toString()
            town = binding.choiceTown.text.toString()
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("theTown", town)
            startActivity(i)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val menuHome = menu!!.findItem(R.id.homeMenu)
        val switchActivity = Intent(this,MainActivity::class.java)
        menuHome.intent = switchActivity
        return super.onCreateOptionsMenu(menu)
    }
}