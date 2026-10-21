package tn.rnu.isetr.projet_medical.data

data class UserRequestDTO(
    val email: String,
    val motDePasse: String,
    val nom: String? = null,
    val prenom: String? = null,
    val confirmMotDePasse: String? = null,
    val role: String? = null,
    val age: Int? = null,
    val telephone: String? = null,
    val image: String? = null
)