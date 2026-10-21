package tn.rnu.isetr.projet_medical.repository

import retrofit2.Response
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.data.UserResponseDTO

class AuthRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun signup(user: UserRequestDTO): Response<UserResponseDTO> {
        return apiService.signup(user)
    }

    suspend fun login(user: UserRequestDTO): Response<UserResponseDTO> {
        return apiService.login(user)
    }
}