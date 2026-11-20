package tn.rnu.isetr.projet_medical.data

import com.google.gson.Gson
import retrofit2.Response

data class ForgotPasswordRequest(val email: String)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val motDePasse: String,
    val confirmMotDePasse: String
)

data class MessageResponse(val message: String)
fun Response<*>.errorMessage(): String =
    try {
        Gson().fromJson(errorBody()?.string(), MessageResponse::class.java)?.message
            ?: "Une erreur est survenue."
    } catch (e: Exception) {
        "Une erreur est survenue."
    }