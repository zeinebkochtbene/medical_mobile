package tn.rnu.isetr.projet_medical.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse


interface ApiService {
    @POST("auth/signup/patient")
    suspend fun signup(@Body request: UserRequestDTO): Response<UserResponseDTO>

    @POST("auth/login")
    suspend fun login(@Body request: UserRequestDTO): Response<UserResponseDTO>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<MessageResponse>

    @GET("api/medecins")
    suspend fun getMedecins(
        @Header("Authorization") token: String? = null
    ): Response<List<MedecinResponseDTO>>

    @GET("api/patient/medecin/{id}/creneaux")
    suspend fun getCreneaux(
        @Path("id") medecinId: Long,
        @Query("date") date: String? = null,
        @Header("Authorization") token: String? = null
    ): Response<List<CreneauResponseDTO>>

    @PATCH("api/creneaux/reserver/{id}")
    suspend fun reserverCreneau(
        @Path("id") id: Long,
        @Header("Authorization") token: String?,
        @Query("resumeChatbot") resumeChatbot: String? = null,
        @Query("specialiteRecommandee") specialiteRecommandee: String? = null
    ): Response<CreneauResponseDTO>

    @GET("api/rendezvous/patient")
    suspend fun getMesRendezVous(
        @Header("Authorization") token: String?
    ): Response<List<RendezVousResponse>>

    @DELETE("api/patient/rendezvous/{id}")
    suspend fun supprimerRendezVous(
        @Path("id") id: Long,
        @Header("Authorization") token: String?
    ): Response<Void>

    @GET("api/specialites")
    suspend fun getSpecialites(
        @Header("Authorization") token: String? = null
    ): Response<List<Specialite>>

    @GET("api/medecins/localisations")
    suspend fun getLocalisations(
        @Header("Authorization") token: String? = null
    ): Response<List<String>>

    @GET("api/medecins/filtrer")
    suspend fun filtrerMedecins(
        @Query("specialiteId") specialiteId: Long?,
        @Query("localisation") localisation: String?,
        @Header("Authorization") token: String? = null
    ): Response<List<MedecinResponseDTO>>


}