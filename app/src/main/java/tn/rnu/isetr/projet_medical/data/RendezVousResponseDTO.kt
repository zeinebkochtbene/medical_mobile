package tn.rnu.isetr.projet_medical.data.model

data class RendezVousResponse(
    val id: Long,
    val confirme: Boolean,
    val nomMedecin: String?,
    val specialite: String?,
    val dateCreneau: String?,
    val heureCreneau: String?,
    val cabinet: String?,
    val resumeChatbot: String? = null
)