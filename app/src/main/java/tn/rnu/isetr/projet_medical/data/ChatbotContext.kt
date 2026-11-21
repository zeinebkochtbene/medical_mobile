package tn.rnu.isetr.projet_medical.data

object ChatbotContext {
    var resume: String? = null
    var specialite: String? = null

    fun clear() {
        resume = null
        specialite = null
    }
}