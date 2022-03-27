package code.with.me.weatherretrofitapp

import code.with.me.weatherretrofitapp.Services.GeoService

class GeoRepository(private val geoService: GeoService) {
    fun getGeo(LATIT: String,LONGT: String,format: String) = geoService.getGeo(LATIT,LONGT,format)
}