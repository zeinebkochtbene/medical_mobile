package tn.rnu.isetr.projet_medical.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/signup/patient")
    suspend fun signup(@Body request: UserRequestDTO): Response<UserResponseDTO>

    @POST("auth/login")
    suspend fun login(@Body request: UserRequestDTO): Response<UserResponseDTO>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<MessageResponse>
}