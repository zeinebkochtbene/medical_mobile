package tn.rnu.isetr.projet_medical.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.MedecinResponseDTO
import tn.rnu.isetr.projet_medical.databinding.ItemMedecinBinding
import android.util.Base64
import android.view.View

private const val BASE_URL = "http://10.0.2.2:8081"

class MedecinAdapter(
    private var medecins: List<MedecinResponseDTO>,
    private val onItemClick: (MedecinResponseDTO) -> Unit
) : RecyclerView.Adapter<MedecinAdapter.MedecinViewHolder>() {

    class MedecinViewHolder(val binding: ItemMedecinBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedecinViewHolder {
        val binding = ItemMedecinBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MedecinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedecinViewHolder, position: Int) {
        val medecin = medecins[position]
        holder.binding.tvNomMedecin.text = "Dr. ${medecin.prenom} ${medecin.nom}"
        holder.binding.tvSpecialite.text = medecin.specialite?.nom ?: "Médecin généraliste"

        holder.binding.imgMedecin.loadMedecinImage(medecin.image)
        holder.binding.tvAdresse.text = medecin.adresse ?: ""
        holder.binding.tvAdresse.visibility =
            if (medecin.adresse.isNullOrBlank()) View.GONE else View.VISIBLE

        // Action sur le bouton "Voir profil >" ou sur la carte entière
        holder.binding.btnVoirProfil.setOnClickListener { onItemClick(medecin) }
        holder.binding.root.setOnClickListener { onItemClick(medecin) }
    }

    override fun getItemCount(): Int = medecins.size

    fun updateData(newMedecins: List<MedecinResponseDTO>) {
        medecins = newMedecins
        notifyDataSetChanged()
    }
}

private fun ImageView.loadMedecinImage(image: String?) {
    when {
        image.isNullOrBlank() -> load(R.drawable.ic_person_placeholder) {
            transformations(CircleCropTransformation())
        }

        // Base64 (avec ou sans préfixe data:image/...)
        image.startsWith("data:image") || image.length > 500 -> {
            val bytes = Base64.decode(image.substringAfter(","), Base64.DEFAULT)
            load(bytes) {
                placeholder(R.drawable.ic_person_placeholder)
                error(R.drawable.ic_person_placeholder)
                transformations(CircleCropTransformation())
            }
        }

        // URL complète ou nom de fichier
        else -> {
            val url = if (image.startsWith("http")) image else "$BASE_URL/uploads/$image"
            load(url) {
                placeholder(R.drawable.ic_person_placeholder)
                error(R.drawable.ic_person_placeholder)
                transformations(CircleCropTransformation())
            }
        }
    }
}