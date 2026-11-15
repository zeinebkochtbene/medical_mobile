package tn.rnu.isetr.projet_medical.ui.creneau

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.projet_medical.data.CreneauResponseDTO
import tn.rnu.isetr.projet_medical.databinding.ItemCreneauBinding

class CreneauAdapter(
    private var creneaux: List<CreneauResponseDTO>,
    private val onClick: (CreneauResponseDTO) -> Unit
) : RecyclerView.Adapter<CreneauAdapter.CreneauViewHolder>() {

    private var selectedId: Long? = null

    class CreneauViewHolder(val binding: ItemCreneauBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreneauViewHolder {
        val binding = ItemCreneauBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CreneauViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CreneauViewHolder, position: Int) {
        val c = creneaux[position]
        // le serveur renvoie "08:00:00" : on garde "08:00"
        holder.binding.tvHeure.text = "${c.heureDebut.take(5)} - ${c.heureFin.take(5)}"
        holder.binding.tvType.text = c.typeSoin ?: "Consultation"

        val choisi = c.id == selectedId
        holder.binding.root.setCardBackgroundColor(
            if (choisi) Color.parseColor("#B2DFDB") else Color.WHITE
        )

        holder.binding.root.setOnClickListener {
            selectedId = c.id
            notifyDataSetChanged()
            onClick(c)
        }
    }

    override fun getItemCount(): Int = creneaux.size

    fun updateData(newCreneaux: List<CreneauResponseDTO>) {
        creneaux = newCreneaux
        notifyDataSetChanged()
    }
}