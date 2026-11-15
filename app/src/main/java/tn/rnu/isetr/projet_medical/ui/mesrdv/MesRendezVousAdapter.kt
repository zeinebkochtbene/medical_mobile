package tn.rnu.isetr.projet_medical.ui.mesrdv

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.projet_medical.data.model.RendezVousResponse
import tn.rnu.isetr.projet_medical.databinding.ItemRendezVousBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MesRendezVousAdapter(
    private var liste: List<RendezVousResponse>,
    private val onDetailsClick: (RendezVousResponse) -> Unit,
    private val onAnnulerClick: (Long) -> Unit
) : RecyclerView.Adapter<MesRendezVousAdapter.RdvViewHolder>() {

    class RdvViewHolder(val binding: ItemRendezVousBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RdvViewHolder {
        val binding = ItemRendezVousBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RdvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RdvViewHolder, position: Int) {
        val rdv = liste[position]
        with(holder.binding) {
            val nomBrut = rdv.nomMedecin ?: "Inconnu"
            tvNomMedecin.text = if (nomBrut.startsWith("Dr.", ignoreCase = true)) nomBrut else "Dr. $nomBrut"

            tvSpecialite.text = rdv.specialite ?: "Généraliste"

            val heureClean = rdv.heureCreneau?.let {
                if (it.length >= 5) it.substring(0, 5) else it
            } ?: ""

            val dateObj = try {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(rdv.dateCreneau ?: "")
            } catch (e: Exception) {
                null
            }

            val dateFormatted = dateObj?.let {
                SimpleDateFormat("d MMMM yyyy", Locale.FRENCH).format(it)
            } ?: (rdv.dateCreneau ?: "")

            tvJour.text = dateObj?.let { SimpleDateFormat("d", Locale.FRENCH).format(it) } ?: "--"
            tvMois.text = dateObj?.let { SimpleDateFormat("MMM", Locale.FRENCH).format(it) } ?: ""

            tvDateHeure.text = "$dateFormatted · $heureClean"
            tvAdresse.text = rdv.cabinet ?: "Cabinet médical"

            if (rdv.confirme) {
                tvStatut.text = "Confirmé"
                tvStatut.setTextColor(Color.parseColor("#00796B"))
                tvStatut.background.setTint(Color.parseColor("#E0F2F1"))
                btnAnnuler.visibility = View.VISIBLE
            } else {
                tvStatut.text = "En attente"
                tvStatut.setTextColor(Color.parseColor("#E65100"))
                tvStatut.background.setTint(Color.parseColor("#FFF3E0"))
                btnAnnuler.visibility = View.VISIBLE
            }

            btnDetails.setOnClickListener { onDetailsClick(rdv) }
            btnAnnuler.setOnClickListener { onAnnulerClick(rdv.id) }
        }
    }

    override fun getItemCount(): Int = liste.size

    fun updateData(nouvelleListe: List<RendezVousResponse>) {
        liste = nouvelleListe
        notifyDataSetChanged()
    }
}