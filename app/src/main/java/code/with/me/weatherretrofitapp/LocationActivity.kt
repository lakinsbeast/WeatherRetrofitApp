package code.with.me.weatherretrofitapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import code.with.me.weatherretrofitapp.databinding.ActivityLocationBinding

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    var town: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.initTown.setOnClickListener {
            town = binding.editTown.text.toString()
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("theTown", town)
            startActivity(i)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val menuHome = menu!!.findItem(R.id.homeMenu)
        val switchActivity = Intent(LocationActivity@this,MainActivity::class.java)
        menuHome.setIntent(switchActivity)
        return super.onCreateOptionsMenu(menu)
    }
}