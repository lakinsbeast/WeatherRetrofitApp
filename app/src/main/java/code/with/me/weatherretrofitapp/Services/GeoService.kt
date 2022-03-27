package code.with.me.weatherretrofitapp.Services

import code.with.me.weatherretrofitapp.Items.GeoItem
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoService {
    @GET("/reverse")

    fun getGeo(@Query("lat") LATIT: String, @Query("lon") LONGT: String, @Query("format") format: String): Call<GeoItem>
    companion object{
        private const val BASE_URL = "https://nominatim.openstreetmap.org"

        fun create(): GeoService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(GeoService::class.java)
        }
    }

}