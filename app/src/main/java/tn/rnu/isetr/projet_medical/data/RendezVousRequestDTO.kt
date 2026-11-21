package tn.rnu.isetr.projet_medical.data

data class RendezVousRequestDTO(
    val patientId: Long,
    val creneauId: Long,
    val resumeChatbot: String? = null,
    val specialiteRecommandee: String? = null
)