package tn.rnu.isetr.projet_medical.data.model

import com.google.gson.annotations.SerializedName

data class RendezVousResponse(
    val id: Long,
    val confirme: Boolean,
    val nomMedecin: String?,
    @SerializedName("specialite")
    val specialite: String?,
    val dateCreneau: String?,
    val heureCreneau: String?,
    val cabinet: String?,
    val resumeChatbot: String? = null
)