package tn.rnu.isetr.projet_medical.ui.search

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.ChatbotContext
import tn.rnu.isetr.projet_medical.data.MedecinResponseDTO
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.databinding.FragmentSearchBinding
import tn.rnu.isetr.projet_medical.ui.creneau.CreneauFragment
import tn.rnu.isetr.projet_medical.viewmodel.MedecinViewModel

class SearchFragment : Fragment() {

    private var resumeChatbot: String? = null
    private var specialiteRecommandee: String? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MedecinAdapter
    private val viewModel: MedecinViewModel by viewModels()

    private var selectedSpecialiteId: Long? = null
    private var selectedLocalisation: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resumeChatbot = arguments?.getString("resume_chatbot")
            ?: requireActivity().intent?.getStringExtra("resume_chatbot")
        specialiteRecommandee = arguments?.getString("specialite_recommandee")
            ?: requireActivity().intent?.getStringExtra("specialite_recommandee")

        adapter = MedecinAdapter(emptyList()) { medecin -> ouvrirCreneaux(medecin) }
        binding.recyclerMedecins.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMedecins.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { etat ->
            when (etat) {
                is Resource.Loading -> Unit
                is Resource.Success -> adapter.updateData(etat.data)
                is Resource.Error -> Toast.makeText(requireContext(), etat.message, Toast.LENGTH_LONG).show()
            }
        }

        binding.etSearch.doOnTextChanged { texte, _, _, _ ->
            viewModel.rechercher(texte.toString())
        }

        setupFilterButtons()
    }

    private fun setupFilterButtons() {
        val rawToken = SessionManager(requireContext()).getToken()
        val token = if (rawToken != null) "Bearer $rawToken" else null

        // 1. Bouton "Toutes"
        binding.btnFilterToutes.setOnClickListener {
            selectedSpecialiteId = null
            selectedLocalisation = null
            binding.btnFilterSpecialite.text = "Spécialité"
            binding.btnFilterLocalisation.text = "Localisation"
            majStyleFiltres()
            viewModel.rechercher("")
        }

        // 2. Bouton "Spécialité"
        binding.btnFilterSpecialite.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.getSpecialites(token)
                    if (response.isSuccessful && response.body() != null) {
                        val specialites = response.body()!!

                        val noms = listOf("Toutes les spécialités") + specialites.map { it.nom.toString() }
                        val indexActuel = if (selectedSpecialiteId == null) 0
                        else specialites.indexOfFirst { it.id == selectedSpecialiteId } + 1

                        afficherChoix("Spécialité", noms, indexActuel) { index ->
                            if (index == 0) {
                                selectedSpecialiteId = null
                                binding.btnFilterSpecialite.text = "Spécialité"
                            } else {
                                val spec = specialites[index - 1]
                                selectedSpecialiteId = spec.id
                                binding.btnFilterSpecialite.text = spec.nom.toString()
                            }
                            majStyleFiltres()
                            effectuerFiltreAPI(token)
                        }
                    }
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3. Bouton "Localisation"
        binding.btnFilterLocalisation.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.getLocalisations(token)
                    if (response.isSuccessful && response.body() != null) {
                        val localisations = response.body()!!

                        val noms = listOf("Toutes les localisations") + localisations
                        val indexActuel = if (selectedLocalisation == null) 0
                        else localisations.indexOf(selectedLocalisation) + 1

                        afficherChoix("Localisation", noms, indexActuel) { index ->
                            if (index == 0) {
                                selectedLocalisation = null
                                binding.btnFilterLocalisation.text = "Localisation"
                            } else {
                                selectedLocalisation = localisations[index - 1]
                                binding.btnFilterLocalisation.text = selectedLocalisation
                            }
                            majStyleFiltres()
                            effectuerFiltreAPI(token)
                        }
                    }
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show()
                }
            }
        }

        majStyleFiltres()
    }

    // Boîte de dialogue Material : coins arrondis, titre, liste à choix unique
    private fun afficherChoix(
        titre: String,
        options: List<String>,
        indexActuel: Int,
        onChoix: (Int) -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(titre)
            .setSingleChoiceItems(options.toTypedArray(), indexActuel) { dialog, index ->
                onChoix(index)
                dialog.dismiss()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // Un filtre actif devient teal plein, un filtre inactif devient blanc avec contour
    private fun majStyleFiltres() {
        styler(binding.btnFilterToutes, selectedSpecialiteId == null && selectedLocalisation == null)
        styler(binding.btnFilterSpecialite, selectedSpecialiteId != null)
        styler(binding.btnFilterLocalisation, selectedLocalisation != null)
    }

    private fun styler(bouton: MaterialButton, actif: Boolean) {
        val teal = requireContext().getColor(R.color.teal_primary)
        if (actif) {
            bouton.backgroundTintList = ColorStateList.valueOf(teal)
            bouton.setTextColor(Color.WHITE)
            bouton.iconTint = ColorStateList.valueOf(Color.WHITE)
            bouton.strokeWidth = 0
        } else {
            bouton.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            bouton.setTextColor(Color.parseColor("#1F2D2B"))
            bouton.iconTint = ColorStateList.valueOf(teal)
            bouton.strokeColor = ColorStateList.valueOf(Color.parseColor("#D5E0DD"))
            bouton.strokeWidth = (1 * resources.displayMetrics.density).toInt()
        }
    }

    private fun effectuerFiltreAPI(token: String?) {
        lifecycleScope.launch {
            try {
                android.util.Log.d("FILTRE_DEBUG", "Envoi filtre -> specialiteId: $selectedSpecialiteId, localisation: $selectedLocalisation")

                val response = RetrofitClient.apiService.filtrerMedecins(
                    specialiteId = selectedSpecialiteId,
                    localisation = selectedLocalisation,
                    token = token
                )

                if (response.isSuccessful && response.body() != null) {
                    val medecinsFiltres = response.body()!!
                    adapter.updateData(medecinsFiltres)

                    if (medecinsFiltres.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Il n'y a pas de médecins pour cette sélection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    android.util.Log.e("FILTRE_DEBUG", "Erreur serveur code: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Erreur serveur (${response.code()})", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("FILTRE_DEBUG", "Exception: ${e.message}")
                Toast.makeText(requireContext(), "Erreur de connexion", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ouvrirCreneaux(m: MedecinResponseDTO) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                CreneauFragment.newInstance(
                    m.id,
                    "Dr. ${m.prenom} ${m.nom}",
                    ChatbotContext.resume,
                    ChatbotContext.specialite
                )
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}