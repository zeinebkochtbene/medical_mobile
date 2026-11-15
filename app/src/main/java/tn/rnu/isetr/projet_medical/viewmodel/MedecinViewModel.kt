package tn.rnu.isetr.projet_medical.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.MedecinResponseDTO
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.repository.MedecinRepository
import java.util.Locale

class MedecinViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = MedecinRepository(SessionManager(app))

    private var allMedecins: List<MedecinResponseDTO> = emptyList()
    private var query = ""

    private val _state = MutableLiveData<Resource<List<MedecinResponseDTO>>>()
    val state: LiveData<Resource<List<MedecinResponseDTO>>> = _state

    init { charger() }

    fun charger() {
        _state.value = Resource.Loading
        viewModelScope.launch {
            when (val r = repo.getMedecins()) {
                is Resource.Success -> {
                    allMedecins = r.data
                    _state.value = Resource.Success(filtrer(query))
                }
                is Resource.Error -> _state.value = r
                is Resource.Loading -> Unit
            }
        }
    }

    fun rechercher(texte: String) {
        query = texte
        if (_state.value is Resource.Loading) return
        _state.value = Resource.Success(filtrer(texte))
    }

    private fun filtrer(texte: String): List<MedecinResponseDTO> {
        val q = texte.lowercase(Locale.getDefault()).trim()
        if (q.isEmpty()) return allMedecins
        return allMedecins.filter { m ->
            m.nom.lowercase(Locale.getDefault()).contains(q) ||
                    m.prenom.lowercase(Locale.getDefault()).contains(q) ||
                    m.specialite?.nom?.lowercase(Locale.getDefault())?.contains(q) == true ||
                    m.adresse?.lowercase(Locale.getDefault())?.contains(q) == true
        }
    }
}