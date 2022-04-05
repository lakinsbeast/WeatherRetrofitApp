package code.with.me.weatherretrofitapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import code.with.me.weatherretrofitapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private var town: String = ""
    private var countries = arrayOf("Paris", "New-York", "London", "Bangkok", "Singapore",
        "Dubai", "Phuket", "Rome", "Tokyo", "Istanbul", "Seoul", "Prague", "Mecca", "Miami",
        "Mumbai", "Barcelona", "Moscow", "Budapest", "Melbourne", "Mexico", "Washington", "Nice",
        "Frankfurt", "Warsaw", "Krakow", "Orlando")

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        binding.ToolbarText.visibility = View.INVISIBLE
        binding.ToolbarText.text = "Выбор местоположения"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.apply {
                    locactivity.setBackgroundColor(Color.WHITE)
                    initTown.setBackgroundColor(Color.BLACK)
                    initTown.setTextColor(Color.WHITE)
                    choiceTown.setBackgroundColor(Color.BLACK)
                }
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.apply {
                    locactivity.setBackgroundColor(Color.BLACK)
                    initTown.setBackgroundColor(Color.WHITE)
                    initTown.setTextColor(Color.BLACK)
                    choiceTown.setBackgroundColor(Color.WHITE)
                    choiceTown.setTextColor(Color.BLACK)
                }
            } // Night mode is active, we're using dark theme
        }



//        val date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
//        Log.d("hours", date.toString())
//        when (date) {
//            0,1,2,3,4,5,6 -> binding.locactivity.setBackgroundResource(R.drawable.evening_forest)
//            7,8,9,10,11,12 -> binding.locactivity.setBackgroundResource(R.drawable.day_forest)
//            13,14,15,16,17,18 -> binding.locactivity.setBackgroundResource(R.drawable.evening_forest)
//            19,20,21,22,23 -> binding.locactivity.setBackgroundResource(R.drawable.night_forest)
//        }

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
        val locationBtn = menu?.findItem(R.id.gpsMenu)
        locationBtn?.isVisible = false
        val switchActivity = Intent(this,MainActivity::class.java)
        return super.onCreateOptionsMenu(menu)
    }
}