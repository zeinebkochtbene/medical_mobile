package tn.rnu.isetr.projet_medical

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tn.rnu.isetr.projet_medical.databinding.ActivityMainBinding
import tn.rnu.isetr.projet_medical.viewmodel.AuthViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.authenticatedUser.observe(this) { user ->
            if (user != null) {
                // Affichage conditionné par les données du ViewModel
                binding.tvBienvenue.text = "Bienvenue ${user.prenom} ${user.nom} !"
            } else {
                binding.tvBienvenue.text = "Bienvenue !"
            }
        }
    }
}