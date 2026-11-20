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
    private val sessionManager = SessionManager(application)
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
                    sessionManager.saveUserName("${loggedUser.prenom} ${loggedUser.nom}".trim())
                    sessionManager.saveUserEmail(loggedUser.email)



                } else {
                    _loginResult.value = Result.failure(Exception("Email ou mot de passe incorrect"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    fun logout() = repository.logout()
}