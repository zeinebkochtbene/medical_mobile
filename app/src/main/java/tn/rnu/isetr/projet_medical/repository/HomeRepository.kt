package tn.rnu.isetr.projet_medical.repository

import kotlinx.coroutines.CancellationException
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import java.io.IOException

class HomeRepository(private val session: SessionManager) {
    private val api = RetrofitClient.apiService

    suspend fun getProchainRendezVous(): Resource<RendezVousResponse?> {
        return try {
            val token = session.getToken()
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

            val response = api.getMesRendezVous(authHeader)
            val body = response.body()
            when {
                response.isSuccessful && body != null -> Resource.Success(body.firstOrNull())
                response.code() == 401 -> Resource.Error("Session expirée, reconnectez-vous")
                else -> Resource.Error("Erreur de chargement (${response.code()})")
            }
        } catch (e: IOException) {
            Resource.Error("Problème de connexion")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Erreur inconnue")
        }
    }
}