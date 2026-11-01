package tn.rnu.isetr.projet_medical.data

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // true si l'application tourne dans l'émulateur Android Studio
    private val isEmulator: Boolean
        get() = Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.contains("emulator") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu")

    // Émulateur : 10.0.2.2 désigne le PC
    // Téléphone USB : localhost (avec la commande adb reverse)
    private val BASE_URL: String
        get() = if (isEmulator) "http://10.0.2.2:8081/" else "http://localhost:8081/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}