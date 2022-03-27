package code.with.me.weatherretrofitapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GeoViewModelFactory(private val repo: GeoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(GeoViewModel::class.java)) {
            GeoViewModel(this.repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}