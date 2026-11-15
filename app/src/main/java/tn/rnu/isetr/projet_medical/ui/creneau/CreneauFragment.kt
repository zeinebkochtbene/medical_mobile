package tn.rnu.isetr.projet_medical.ui.creneau

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.databinding.FragmentCreneauBinding
import tn.rnu.isetr.projet_medical.viewmodel.CreneauViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreneauFragment : Fragment() {

    private var _binding: FragmentCreneauBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreneauViewModel by viewModels()
    private lateinit var adapter: CreneauAdapter
    private var medecinId = 0L
    private var dateChoisie: Calendar = Calendar.getInstance()   // aujourd'hui par défaut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        medecinId = requireArguments().getLong(ARG_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreneauBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMedecin.text = requireArguments().getString(ARG_NOM)

        binding.cardDate.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { _, annee, mois, jour ->
                    dateChoisie = Calendar.getInstance().apply { set(annee, mois, jour) }
                    chargerPourDateChoisie()
                },
                dateChoisie.get(Calendar.YEAR),
                dateChoisie.get(Calendar.MONTH),
                dateChoisie.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000   // pas de dates passées
            dialog.show()
        }

        adapter = CreneauAdapter(emptyList()) { creneau ->
            viewModel.reserverCreneau(creneau.id)
        }
        binding.recyclerCreneaux.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerCreneaux.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { etat ->
            binding.progressBar.visibility = if (etat is Resource.Loading) View.VISIBLE else View.GONE
            when (etat) {
                is Resource.Loading -> binding.tvVide.visibility = View.GONE
                is Resource.Success -> {
                    adapter.updateData(etat.data)
                    binding.tvVide.visibility = if (etat.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    adapter.updateData(emptyList())
                    binding.tvVide.visibility = View.GONE
                    Toast.makeText(requireContext(), etat.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.reservationState.observe(viewLifecycleOwner) { etat ->
            when (etat) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Rendez-vous réservé avec succès !", Toast.LENGTH_SHORT).show()
                    chargerPourDateChoisie()
                }
                is Resource.Error -> Toast.makeText(requireContext(), etat.message, Toast.LENGTH_LONG).show()
                else -> {}
            }
        }

        chargerPourDateChoisie()
    }

    private fun chargerPourDateChoisie() {
        binding.tvDate.text = dateLisible(dateChoisie)
        viewModel.chargerParDate(medecinId, dateIso(dateChoisie))
    }

    // "2026-11-12" : Locale.US pour garder des chiffres 0-9 quelle que soit la langue du téléphone
    private fun dateIso(c: Calendar): String = String.format(
        Locale.US, "%04d-%02d-%02d",
        c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH)
    )

    // "Jeudi 12 novembre 2026"
    private fun dateLisible(c: Calendar): String =
        SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH).format(c.time)
            .replaceFirstChar { it.uppercase() }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ID = "medecinId"
        private const val ARG_NOM = "medecinNom"

        fun newInstance(medecinId: Long, medecinNom: String) = CreneauFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_ID, medecinId)
                putString(ARG_NOM, medecinNom)
            }
        }
    }
}