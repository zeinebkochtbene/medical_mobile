package tn.rnu.isetr.projet_medical.data

import android.os.Build
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
object RetrofitClient {

    private val isEmulator: Boolean
        get() = Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.contains("emulator") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu")

    private val BASE_URL: String
        get() = if (isEmulator) "http://10.0.2.2:8081/" else "http://localhost:8081/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            //genere ApiService
            .create(ApiService::class.java)
    }
    fun getChatbotApi(context: Context): ChatbotApi {
        val appContext = context.applicationContext
        val sessionManager = SessionManager(appContext)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = sessionManager.getToken()
                val requestBuilder = chain.request().newBuilder()
                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatbotApi::class.java)
    }
}