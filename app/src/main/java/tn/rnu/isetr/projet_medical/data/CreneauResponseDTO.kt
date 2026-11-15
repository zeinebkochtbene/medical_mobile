package tn.rnu.isetr.projet_medical.data

data class CreneauResponseDTO(
    val id: Long,
    val heureDebut: String,
    val heureFin: String,
    val disponible: Boolean,
    val typeSoin: String? = null,
    val date: String? = null
)