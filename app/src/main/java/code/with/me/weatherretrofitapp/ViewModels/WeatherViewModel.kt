package code.with.me.weatherretrofitapp.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import code.with.me.weatherretrofitapp.Items.WeatherItem
import code.with.me.weatherretrofitapp.Repositories.WeatherRepository
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel(private val repo: WeatherRepository) : ViewModel() {
    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val tempC: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val textTitle: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val feelsLikeTitle: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val lastUpdateTitleData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val urlImage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val response: MutableLiveData<WeatherItem> by lazy {
        MutableLiveData<WeatherItem>()
    }
    var job: Job? = null
    val town: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }


    fun initWeather() {
            val response =
                repo.getPosts("a84e682c84344905943193456221202", town.value.toString(), "ru")
            Log.d("responseДоEnqueue", response.toString())
            response.enqueue(object : Callback<WeatherItem> {
                override fun onResponse(
                    call: Call<WeatherItem>,
                    response: Response<WeatherItem>
                ) {
                    if (response.isSuccessful && town.value != "null") {
                        Log.d("viewModelTown", town.toString())
                        Log.d("response", response.body()!!.toString())
                        town.value = response.body()!!.loc.name.toString()
                        feelsLikeTitle.value = response.body()!!.cur.feelslike_c.toString()
                        lastUpdateTitleData.value = response.body()!!.cur.last_updated.toString()
                        textTitle.value = response.body()!!.cur.condition.text.toString()
                        tempC.value = response.body()!!.cur.temp_c.toString()
                        urlImage.value = response.body()!!.cur.condition.icon.toString()
                    } else {
                        Log.d("viewModelTown", town.toString())
                        Log.d("response", response.errorBody().toString())
                        town.value = "Не выбран город"
                    }
                }

                override fun onFailure(call: Call<WeatherItem>, t: Throwable) {
                    Log.d("responseWthr", "вызвался onFailure")
                }
            })
        }


    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}