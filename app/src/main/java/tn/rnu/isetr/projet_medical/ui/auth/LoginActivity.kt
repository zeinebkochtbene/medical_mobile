package tn.rnu.isetr.projet_medical.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope // <-- Indispensable
import kotlinx.coroutines.launch       // <-- Indispensable
import tn.rnu.isetr.projet_medical.MainActivity
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = UserRequestDTO(
                email = email,
                motDePasse = password
            )

            // Appel de la fonction suspend dans une coroutine
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(loginRequest)
                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!
                        Toast.makeText(this@LoginActivity, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Erreur réseau : ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}