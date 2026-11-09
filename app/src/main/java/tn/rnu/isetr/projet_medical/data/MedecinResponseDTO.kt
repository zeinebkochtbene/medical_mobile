package tn.rnu.isetr.projet_medical.data

data class SpecialiteDTO(
    val id: Long,
    val nom: String
)

data class MedecinResponseDTO(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val approved: Boolean,
    val numeroOrdre: String,
    val specialite: SpecialiteDTO?
)