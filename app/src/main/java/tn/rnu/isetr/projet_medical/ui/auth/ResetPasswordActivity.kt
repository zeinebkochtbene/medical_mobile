package tn.rnu.isetr.projet_medical.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.ResetPasswordRequest
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.errorMessage
import tn.rnu.isetr.projet_medical.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email").orEmpty()

        binding.btnValidate.setOnClickListener {
            val code = binding.etCode.text.toString().trim()
            val mdp = binding.etNewPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()

            if (code.length != 6) {
                showMessage("Saisissez le code à 6 chiffres reçu par e-mail.")
                return@setOnClickListener
            }

            binding.btnValidate.isEnabled = false
            binding.tvMessage.visibility = View.GONE

            lifecycleScope.launch {
                try {
                    val r = RetrofitClient.apiService.resetPassword(
                        ResetPasswordRequest(email, code, mdp, confirm)
                    )
                    if (r.isSuccessful) {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            r.body()?.message ?: "Mot de passe modifié.",
                            Toast.LENGTH_LONG
                        ).show()
                        val i = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                        finish()
                    } else {
                        showMessage(r.errorMessage())
                    }
                } catch (e: Exception) {
                    showMessage("Erreur réseau : ${e.message}")
                }
                binding.btnValidate.isEnabled = true
            }
        }
    }

    private fun showMessage(text: String) {
        binding.tvMessage.text = text
        binding.tvMessage.visibility = View.VISIBLE
    }
}