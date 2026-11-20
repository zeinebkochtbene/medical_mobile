package tn.rnu.isetr.projet_medical.repository

import retrofit2.Response
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.data.UserResponseDTO

class AuthRepository (private val session: SessionManager) {
    private val apiService = RetrofitClient.apiService

    suspend fun signup(user: UserRequestDTO): Response<UserResponseDTO> {
        return apiService.signup(user)
    }

    suspend fun login(user: UserRequestDTO): Response<UserResponseDTO> {
        val response = apiService.login(user)
        if (response.isSuccessful) {
            response.body()?.token?.let { session.saveToken(it) }
        }
        return response
    }

    fun logout() = session.clear()

    //si l'utilisateur est deja connecter
    fun isLoggedIn(): Boolean = session.isLoggedIn()
}