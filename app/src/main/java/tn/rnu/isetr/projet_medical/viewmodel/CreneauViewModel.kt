package tn.rnu.isetr.projet_medical.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.CreneauResponseDTO
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.repository.CreneauRepository

class CreneauViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CreneauRepository(SessionManager(app))

    private val _state = MutableLiveData<Resource<List<CreneauResponseDTO>>>()
    val state: LiveData<Resource<List<CreneauResponseDTO>>> = _state

    private val _reservationState = MutableLiveData<Resource<CreneauResponseDTO>>()
    val reservationState: LiveData<Resource<CreneauResponseDTO>> = _reservationState

    fun charger(medecinId: Long) {
        _state.value = Resource.Loading
        viewModelScope.launch {
            _state.value = when (val r = repo.getCreneaux(medecinId)) {
                // on masque les créneaux déjà réservés
                is Resource.Success -> Resource.Success(r.data.filter { it.disponible })
                else -> r
            }
        }
    }

    fun chargerParDate(medecinId: Long, date: String) {
        _state.value = Resource.Loading
        viewModelScope.launch {
            _state.value = when (val r = repo.getCreneauxParDate(medecinId, date)) {
                is Resource.Success -> Resource.Success(r.data.filter { it.disponible })
                else -> r
            }
        }
    }

    fun reserverCreneau(id: Long) {
        _reservationState.value = Resource.Loading
        viewModelScope.launch {
            _reservationState.value = repo.reserverCreneau(id)
        }
    }
}