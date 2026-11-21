package tn.rnu.isetr.projet_medical.data

data class ChatResponseDto(
    val reponse: String,
    val reponsesRapides: List<String> = emptyList(),
    val specialites: List<String> = emptyList(),
    val termine: Boolean = false,
    val resume: String? = null
)
