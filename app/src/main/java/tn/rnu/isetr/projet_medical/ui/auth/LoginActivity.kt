package tn.rnu.isetr.projet_medical.ui.auth
import tn.rnu.isetr.projet_medical.viewmodel.AuthViewModel
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import tn.rnu.isetr.projet_medical.MainActivity
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // 1. Observer le résultat de la connexion
        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.onFailure { error ->
                Toast.makeText(
                    this,
                    error.message ?: "Email ou mot de passe incorrect",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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
            authViewModel.login(loginRequest)
        }

        binding.btnGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}