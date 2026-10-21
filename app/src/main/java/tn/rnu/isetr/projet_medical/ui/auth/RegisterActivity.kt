package tn.rnu.isetr.projet_medical.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.databinding.ActivityRegisterBinding
import tn.rnu.isetr.projet_medical.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnGoLogin.setOnClickListener {
            finish()
        }// Action du bouton d'inscription
        binding.btnRegister.setOnClickListener {
            val nom = binding.etNom.text.toString().trim()
            val prenom = binding.etPrenom.text.toString().trim()
            val ageStr = binding.etAge.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val telephone = binding.etTelephone.text.toString().trim()

            // Vérifications basiques
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = ageStr.toIntOrNull()

            // Création du DTO patient
            val userRequest = UserRequestDTO(
                nom = nom,
                prenom = prenom,
                email = email,
                motDePasse = password,
                confirmMotDePasse = confirmPassword,
                role = "PATIENT",
                age = age,
                telephone = telephone,
                image = null
            )

            // Appel de la fonction du ViewModel
            authViewModel.signup(userRequest)
        }

        // Observation du résultat de l'inscription
        authViewModel.registerResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_LONG).show()
                finish() // Retour à la page de login après succès
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erreur d'inscription"
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }
}