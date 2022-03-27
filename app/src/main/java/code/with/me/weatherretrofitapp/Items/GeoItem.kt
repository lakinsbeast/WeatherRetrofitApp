package code.with.me.weatherretrofitapp.Items


data class GeoItem(
    val address: Address,
    val lat: String,
    val lon: String
)
data class Address(
    val city: String
)