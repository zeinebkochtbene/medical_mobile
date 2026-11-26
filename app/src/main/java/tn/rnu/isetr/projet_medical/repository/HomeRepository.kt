import kotlinx.coroutines.CancellationException
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeRepository(private val session: SessionManager) {
    private val api = RetrofitClient.apiService

    suspend fun getProchainRendezVous(): Resource<RendezVousResponse?> {
        return try {
            val token = session.getToken()
            val authHeader = if (!token.isNullOrEmpty()) "Bearer $token" else null

            val response = api.getMesRendezVous(authHeader)
            val body = response.body()
            when {
                response.isSuccessful && body != null -> Resource.Success(choisirProchain(body))
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

    // Garde les rendez-vous à venir, puis prend le plus proche
    private fun choisirProchain(liste: List<RendezVousResponse>): RendezVousResponse? {
        // "2026-11-26 14:30" : ce format se compare correctement comme du texte
        val maintenant = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())

        return liste
            .filter { !it.dateCreneau.isNullOrBlank() }
            .map { rdv -> rdv to "${rdv.dateCreneau} ${rdv.heureCreneau?.take(5) ?: "00:00"}" }
            .filter { (_, debut) -> debut >= maintenant }
            .minByOrNull { (_, debut) -> debut }
            ?.first
    }
}