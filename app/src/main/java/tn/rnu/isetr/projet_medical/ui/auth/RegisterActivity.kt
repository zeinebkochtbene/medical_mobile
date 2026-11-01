package tn.rnu.isetr.projet_medical.ui.auth

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tn.rnu.isetr.projet_medical.data.UserRequestDTO
import tn.rnu.isetr.projet_medical.databinding.ActivityRegisterBinding
import tn.rnu.isetr.projet_medical.viewmodel.AuthViewModel
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoLogin.setOnClickListener { finish() }

        // Choix de la date de naissance avec un calendrier
        binding.etDateNaissance.setOnClickListener { pickDate() }

        binding.btnRegister.setOnClickListener {
            val nom = binding.etNom.text.toString().trim()
            val prenom = binding.etPrenom.text.toString().trim()
            val dateNaissance = binding.etDateNaissance.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val telephone = binding.etTelephone.text.toString().trim()

            val sexe = when {
                binding.rbHomme.isChecked -> "HOMME"
                binding.rbFemme.isChecked -> "FEMME"
                else -> null
            }

            // Vérifications basiques
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (dateNaissance.isEmpty()) {
                Toast.makeText(this, "Choisissez votre date de naissance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (sexe == null) {
                Toast.makeText(this, "Choisissez votre sexe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userRequest = UserRequestDTO(
                nom = nom,
                prenom = prenom,
                email = email,
                motDePasse = password,
                confirmMotDePasse = confirmPassword,
                role = "PATIENT",
                sexe = sexe,
                dateNaissance = dateNaissance,
                telephone = telephone,
                image = null
            )

            authViewModel.signup(userRequest)
        }

        // Résultat de l'inscription
        authViewModel.registerResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_LONG).show()
                finish() // retour au login
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Erreur d'inscription"
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun pickDate() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -20) // date proposée par défaut : il y a 20 ans

        val dialog = DatePickerDialog(
            this,
            { _, y, m, d ->
                binding.etDateNaissance.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() // pas de date dans le futur
        dialog.show()
    }
}