package tn.rnu.isetr.projet_medical.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.SessionManager
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import tn.rnu.isetr.projet_medical.databinding.FragmentHomeBinding
import tn.rnu.isetr.projet_medical.ui.search.SearchFragment
import tn.rnu.isetr.projet_medical.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private var rdvAffiche: RendezVousResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nom = SessionManager(requireContext()).getUserName() ?: "Patient"
        binding.tvWelcome.text = "👋 Bonjour, $nom."

        binding.cardSearch.setOnClickListener { ouvrirRecherche() }
        binding.cardSpecialites.setOnClickListener { ouvrirRecherche() }
        binding.btnNouveau.setOnClickListener { ouvrirRecherche() }
        binding.btnDetails.setOnClickListener { afficherDetails() }
        binding.cardMesRdv.setOnClickListener {
            Toast.makeText(requireContext(), "Écran « Mes rendez-vous » : bientôt disponible", Toast.LENGTH_SHORT).show()
        }

        viewModel.prochainRdv.observe(viewLifecycleOwner) { etat ->
            when (etat) {
                is Resource.Loading -> montrerMessage("Chargement…")
                is Resource.Error -> montrerMessage(etat.message)
                is Resource.Success -> {
                    val rdv = etat.data
                    if (rdv == null) {
                        montrerMessage("Aucun rendez-vous à venir.\nPrenez rendez-vous avec un médecin.")
                    } else {
                        afficherRdv(rdv)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.charger()   // recharge à chaque retour sur l'accueil
    }

    private fun montrerMessage(texte: String) {
        rdvAffiche = null
        binding.contenuRdv.visibility = View.GONE
        binding.tvEtatRdv.visibility = View.VISIBLE
        binding.tvEtatRdv.text = texte
    }

    private fun afficherRdv(rdv: RendezVousResponse) {
        rdvAffiche = rdv
        binding.tvEtatRdv.visibility = View.GONE
        binding.contenuRdv.visibility = View.VISIBLE

        binding.tvDoctorName.text = "Dr. ${rdv.nomMedecin ?: "Non spécifié"}"
        binding.tvDoctorSpeciality.text = rdv.specialite ?: "Médecin généraliste"
        binding.tvRdvDate.text = "${formatDate(rdv.dateCreneau)} à ${rdv.heureCreneau?.take(5) ?: "--"}"
        binding.tvRdvLocation.text = rdv.cabinet ?: "Cabinet médical"

        if (rdv.confirme) {
            binding.tvStatut.text = "✔ Confirmé"
            binding.tvStatut.setTextColor(Color.parseColor("#00796B"))
        } else {
            binding.tvStatut.text = "⏳ En attente de confirmation"
            binding.tvStatut.setTextColor(Color.parseColor("#E65100"))
        }
    }

    private fun afficherDetails() {
        val rdv = rdvAffiche ?: return
        val message = """
            Médecin : Dr. ${rdv.nomMedecin ?: "-"}
            Spécialité : ${rdv.specialite ?: "-"}
            Date : ${formatDate(rdv.dateCreneau)}
            Heure : ${rdv.heureCreneau?.take(5) ?: "-"}
            Lieu : ${rdv.cabinet ?: "-"}
            Statut : ${if (rdv.confirme) "Confirmé" else "En attente de confirmation"}
        """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Détails du rendez-vous")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun ouvrirRecherche() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SearchFragment())
            .addToBackStack(null)
            .commit()
    }

    // "2026-11-12" -> "Jeudi 12 novembre 2026"
    private fun formatDate(iso: String?): String {
        if (iso.isNullOrBlank()) return "--"
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso)!!
            SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(date)
                .replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            iso
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}