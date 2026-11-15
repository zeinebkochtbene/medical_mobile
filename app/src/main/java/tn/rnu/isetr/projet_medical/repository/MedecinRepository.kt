package tn.rnu.isetr.projet_medical.repository

import kotlinx.coroutines.CancellationException
import tn.rnu.isetr.projet_medical.data.MedecinResponseDTO
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import java.io.IOException

class MedecinRepository(private val session: SessionManager) {
    private val api = RetrofitClient.apiService

    suspend fun getMedecins(): Resource<List<MedecinResponseDTO>> = try {
        val token = session.getToken()
        val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

        val response = api.getMedecins(authHeader)
        val body = response.body()
        if (response.isSuccessful && body != null) Resource.Success(body)
        else Resource.Error("Erreur de chargement (${response.code()})")
    } catch (e: IOException) {
        Resource.Error("Problème de connexion")
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Resource.Error(e.message ?: "Erreur inconnue")
    }
}