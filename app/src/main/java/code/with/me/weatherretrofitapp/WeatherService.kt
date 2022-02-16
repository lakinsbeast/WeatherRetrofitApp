package code.with.me.weatherretrofitapp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface WeatherService {
    @GET("/v1/forecast.json")

    fun getPosts(@Query("key") API_KEY: String, @Query("q") q: String, @Query("lang") lang: String): Call<WeatherItem>

    companion object {
        private const val BASE_URL = "https://api.weatherapi.com"

        fun create(): WeatherService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(WeatherService::class.java)
        }
    }

}