package tn.rnu.isetr.projet_medical.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import tn.rnu.isetr.projet_medical.repository.HomeRepository

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = HomeRepository(SessionManager(app))

    private val _prochainRdv = MutableLiveData<Resource<RendezVousResponse?>>()
    val prochainRdv: LiveData<Resource<RendezVousResponse?>> = _prochainRdv

    fun charger() {
        _prochainRdv.value = Resource.Loading
        viewModelScope.launch { _prochainRdv.value = repo.getProchainRendezVous() }
    }
}