package tn.rnu.isetr.projet_medical.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.data.ForgotPasswordRequest
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.errorMessage
import tn.rnu.isetr.projet_medical.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                binding.etEmail.error = "Saisissez votre e-mail"
                return@setOnClickListener
            }

            binding.btnSend.isEnabled = false
            binding.tvInfo.visibility = View.GONE

            lifecycleScope.launch {
                try {
                    val r = RetrofitClient.apiService.forgotPassword(ForgotPasswordRequest(email))
                    if (r.isSuccessful) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Si un compte existe, un code a été envoyé par e-mail.",
                            Toast.LENGTH_LONG
                        ).show()
                        val i = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        i.putExtra("email", email)
                        startActivity(i)
                        finish()
                    } else {
                        binding.tvInfo.text = r.errorMessage()
                        binding.tvInfo.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    binding.tvInfo.text = "Erreur réseau : ${e.message}"
                    binding.tvInfo.visibility = View.VISIBLE
                }
                binding.btnSend.isEnabled = true
            }
        }
    }
}