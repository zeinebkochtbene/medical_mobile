package tn.rnu.isetr.projet_medical.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse

class MesRendezVousViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)

    private val _state = MutableLiveData<Resource<List<RendezVousResponse>>>()
    val state: LiveData<Resource<List<RendezVousResponse>>> get() = _state

    private val _cancelState = MutableLiveData<Resource<Boolean>>()
    val cancelState: LiveData<Resource<Boolean>> get() = _cancelState

    init {
        chargerRendezVous()
    }

    fun chargerRendezVous() {
        _state.value = Resource.Loading
        viewModelScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken() ?: ""}"
                val response = RetrofitClient.apiService.getMesRendezVous(token)
                if (response.isSuccessful && response.body() != null) {
                    _state.value = Resource.Success(response.body()!!)
                } else {
                    _state.value = Resource.Error("Erreur lors du chargement (${response.code()})")
                }
            } catch (e: Exception) {
                _state.value = Resource.Error(e.localizedMessage ?: "Erreur réseau")
            }
        }
    }

    fun annulerRendezVous(id: Long) {
        viewModelScope.launch {
            try {
                val token = "Bearer ${sessionManager.getToken() ?: ""}"
                val response = RetrofitClient.apiService.supprimerRendezVous(id, token)
                if (response.isSuccessful) {
                    _cancelState.value = Resource.Success(true)
                    chargerRendezVous()
                } else {
                    _cancelState.value = Resource.Error("Impossible d'annuler le rendez-vous")
                }
            } catch (e: Exception) {
                _cancelState.value = Resource.Error(e.localizedMessage ?: "Erreur réseau")
            }
        }
    }
}