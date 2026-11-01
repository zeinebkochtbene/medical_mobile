package tn.rnu.isetr.projet_medical.data

data class UserRequestDTO(
    val email: String,
    val motDePasse: String,
    val nom: String? = null,
    val prenom: String? = null,
    val confirmMotDePasse: String? = null,
    val role: String? = null,
    val sexe: String? = null,            // "HOMME" ou "FEMME"
    val dateNaissance: String? = null,   // "aaaa-mm-jj"
    val telephone: String? = null,
    val image: String? = null
)