package code.with.me.weatherretrofitapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import code.with.me.weatherretrofitapp.Repositories.GeoRepository

class GeoViewModelFactory(private val repo: GeoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GeoViewModel::class.java)) {
            GeoViewModel(this.repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}