package code.with.me.weatherretrofitapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import code.with.me.weatherretrofitapp.Items.GeoItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeoViewModel(private val repo: GeoRepository): ViewModel() {
    val latit: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val longt: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val town: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }



    fun initGeo(){
        val response = repo.getGeo(latit.value.toString(),longt.value.toString(),"json")
        response.enqueue(object: Callback<GeoItem> {
            override fun onResponse(call: Call<GeoItem>, response: Response<GeoItem>) {
                if (response.isSuccessful) {
                    town.value = response.body()!!.address.city
                    Log.d("GeoResponse", response.body()?.address?.city.toString())
                }
            }

            override fun onFailure(call: Call<GeoItem>, t: Throwable) {
                Log.d("responseGeo", "вызвался onFailure")
            }

        })
    }
}