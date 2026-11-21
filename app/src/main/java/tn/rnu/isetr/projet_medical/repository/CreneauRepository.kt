package tn.rnu.isetr.projet_medical.repository

import kotlinx.coroutines.CancellationException
import tn.rnu.isetr.projet_medical.data.CreneauResponseDTO
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import java.io.IOException

class CreneauRepository(private val session: SessionManager) {
    private val api = RetrofitClient.apiService

    suspend fun getCreneaux(medecinId: Long): Resource<List<CreneauResponseDTO>> {
        return try {
            val token = session.getToken()
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

            val response = api.getCreneaux(medecinId, null, authHeader)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else {
                Resource.Error("Erreur de chargement (${response.code()})")
            }
        } catch (e: IOException) {
            Resource.Error("Problème de connexion")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Erreur inconnue")
        }
    }

    suspend fun getCreneauxParDate(medecinId: Long, date: String): Resource<List<CreneauResponseDTO>> {
        return try {
            val token = session.getToken()
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

            // On appelle getCreneaux en passant la date en paramètre
            val response = api.getCreneaux(medecinId, date, authHeader)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else {
                Resource.Error("Erreur lors du chargement par date (${response.code()})")
            }
        } catch (e: IOException) {
            Resource.Error("Problème de connexion")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Erreur inconnue")
        }
    }

    suspend fun reserverCreneau(
        id: Long,
        resumeChatbot: String? = null,
        specialiteRecommandee: String? = null
    ): Resource<CreneauResponseDTO> {
        return try {
            val token = session.getToken()
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

            val response = api.reserverCreneau(
                id = id,
                token = authHeader,
                resumeChatbot = resumeChatbot,
                specialiteRecommandee = specialiteRecommandee
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else {
                Resource.Error("Erreur lors de la réservation (${response.code()})")
            }
        } catch (e: IOException) {
            Resource.Error("Problème de connexion")
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Resource.Error(e.message ?: "Erreur inconnue")
        }
    }
}