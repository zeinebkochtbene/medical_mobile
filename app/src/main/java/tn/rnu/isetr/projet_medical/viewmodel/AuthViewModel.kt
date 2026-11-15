package tn.rnu.isetr.projet_medical.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.data.UserResponseDTO
import tn.rnu.isetr.projet_medical.data.errorMessage
import tn.rnu.isetr.projet_medical.repository.AuthRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // 1. Initialisation du SessionManager avec le contexte de l'application
    private val sessionManager = SessionManager(application)

    // 2. Passage du sessionManager au repository pour que le token soit enregistré automatiquement
    private val repository = AuthRepository(sessionManager)

    private val _registerResult = MutableLiveData<Result<UserResponseDTO>>()
    val registerResult: LiveData<Result<UserResponseDTO>> get() = _registerResult

    fun signup(user: UserRequestDTO) {
        viewModelScope.launch {
            try {
                val response = repository.signup(user)
                if (response.isSuccessful && response.body() != null) {
                    _registerResult.value = Result.success(response.body()!!)
                } else {
                    _registerResult.value = Result.failure(Exception(response.errorMessage()))
                }
            } catch (e: Exception) {
                _registerResult.value = Result.failure(e)
            }
        }
    }

    // --- POUR LA CONNEXION ---
    private val _loginResult = MutableLiveData<Result<UserResponseDTO>>()
    val loginResult: LiveData<Result<UserResponseDTO>> get() = _loginResult

    private val _authenticatedUser = MutableLiveData<UserResponseDTO?>()
    val authenticatedUser: LiveData<UserResponseDTO?> get() = _authenticatedUser

    fun login(user: UserRequestDTO) {
        viewModelScope.launch {
            try {
                val response = repository.login(user)
                if (response.isSuccessful && response.body() != null) {
                    val loggedUser = response.body()!!
                    _loginResult.value = Result.success(loggedUser)
                    _authenticatedUser.value = loggedUser

                    // --- SAUVEGARDE DANS LE SESSIONMANAGER ---
                    sessionManager.saveUserName(loggedUser.nom)    // Remplacez .nom par .name selon votre UserResponseDTO
                    sessionManager.saveUserEmail(loggedUser.email) // Enregistre l'email
                    // (Le token est généralement déjà sauvegardé par le repository, mais vous pouvez ajouter sessionManager.saveToken(...) si nécessaire)
                    // ------------------------------------------

                } else {
                    _loginResult.value = Result.failure(Exception("Email ou mot de passe incorrect"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    // Fonctions bonus pour vérifier la session ou se déconnecter si besoin
    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    fun logout() = repository.logout()
}