package tn.rnu.isetr.projet_medical.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {
    @POST("api/chatbot/message")
    fun envoyer(@Body request: ChatRequestDto): Call<ChatResponseDto>
}