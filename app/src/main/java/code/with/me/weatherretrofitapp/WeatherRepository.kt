package code.with.me.weatherretrofitapp

class WeatherRepository(private val weatherService: WeatherService) {

    fun getPosts(API_KEY: String, q: String, lang: String) = weatherService.getPosts(API_KEY, q, lang)
}