package code.with.me.weatherretrofitapp.Items

import com.google.gson.annotations.SerializedName


data class WeatherItem(
    @SerializedName("location")
    var loc: Location,
    @SerializedName("current") // если не добавить эту аннотацию, то будет вылетать приложение
    var cur: Current,
)
data class Location(
    val name: String,
    val region: String,
    val country: String,
)
data class Current(
    val temp_c: Double,
    val last_updated: String,
    val feelslike_c: Double,
    @SerializedName("condition")
    val condition: Condition
)
data class Condition (
    val text: String,
    val icon: String
        )
