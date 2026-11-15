package tn.rnu.isetr.projet_medical.ui.mesrdv

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.Resource
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import tn.rnu.isetr.projet_medical.databinding.FragmentMesRendezVousBinding
import tn.rnu.isetr.projet_medical.ui.search.SearchFragment
import tn.rnu.isetr.projet_medical.viewmodel.MesRendezVousViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MesRendezVousFragment : Fragment() {

    private var _binding: FragmentMesRendezVousBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MesRendezVousViewModel by viewModels()
    private lateinit var adapter: MesRendezVousAdapter
    private var allRendezVous: List<RendezVousResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMesRendezVousBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MesRendezVousAdapter(
            liste = emptyList(),
            onDetailsClick = { rdv ->
                val statutText = if (rdv.confirme) "Confirmé" else "En attente"
                AlertDialog.Builder(requireContext())
                    .setTitle("Détails du rendez-vous")
                    .setMessage(
                        "Médecin : ${rdv.nomMedecin}\n" +
                                "Spécialité : ${rdv.specialite}\n" +
                                "Date : ${rdv.dateCreneau} à ${rdv.heureCreneau}\n" +
                                "Lieu : ${rdv.cabinet}\n" +
                                "Statut : $statutText"
                    )
                    .setPositiveButton("Fermer", null)
                    .show()
            },
            onAnnulerClick = { rdvId ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Annuler le rendez-vous")
                    .setMessage("Voulez-vous vraiment annuler ce rendez-vous ?")
                    .setPositiveButton("Oui, annuler") { _, _ ->
                        viewModel.annulerRendezVous(rdvId)
                    }
                    .setNegativeButton("Non", null)
                    .show()
            }
        )
        binding.recyclerViewRdv.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRdv.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("À venir"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Passés"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Annulés"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filtrerListe(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        binding.fabNouveauRdv.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SearchFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.btnPrendreRdv.setOnClickListener { binding.fabNouveauRdv.performClick() }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvVide.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    allRendezVous = resource.data ?: emptyList()
                    filtrerListe(binding.tabLayout.selectedTabPosition)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.cancelState.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                Toast.makeText(requireContext(), "Rendez-vous annulé", Toast.LENGTH_SHORT).show()
            } else if (resource is Resource.Error) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrerListe(position: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDateStr = dateFormat.format(Date())

        val listeFiltree = when (position) {
            0 -> {
                // À venir : date >= aujourd'hui
                allRendezVous.filter { rdv ->
                    val dateRdv = rdv.dateCreneau ?: ""
                    dateRdv.isEmpty() || dateRdv >= currentDateStr
                }
            }
            1 -> {
                // Passés : date < aujourd'hui
                allRendezVous.filter { rdv ->
                    val dateRdv = rdv.dateCreneau ?: ""
                    dateRdv.isNotEmpty() && dateRdv < currentDateStr
                }
            }
            2 -> {
                // Annulés
                emptyList()
            }
            else -> allRendezVous
        }

        adapter.updateData(listeFiltree)
        binding.tvVide.visibility = if (listeFiltree.isEmpty()) View.VISIBLE else View.GONE
    }
    override fun onResume() {
        super.onResume()
        viewModel.chargerRendezVous()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}