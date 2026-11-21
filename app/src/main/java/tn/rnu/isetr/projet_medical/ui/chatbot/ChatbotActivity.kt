package tn.rnu.isetr.projet_medical.ui.chatbot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.rnu.isetr.projet_medical.MainActivity
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.ChatMessageDto
import tn.rnu.isetr.projet_medical.data.ChatRequestDto
import tn.rnu.isetr.projet_medical.data.ChatResponseDto
import tn.rnu.isetr.projet_medical.data.ChatbotApi
import tn.rnu.isetr.projet_medical.data.ChatbotContext
import tn.rnu.isetr.projet_medical.data.RetrofitClient
import tn.rnu.isetr.projet_medical.databinding.ActivityChatbotBinding

class ChatbotActivity : AppCompatActivity() {


    private var specialiteChoisie: String? = null
    private var resumeChatbot: String? = null
    private lateinit var binding: ActivityChatbotBinding

    private val messages = mutableListOf<ChatMessageDto>()
    private lateinit var adapter: ChatAdapter
    private lateinit var api: ChatbotApi



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // À ADAPTER : vérifie le nom de ton instance Retrofit dans data/RetrofitClient
        api = RetrofitClient.getChatbotApi(this)

        adapter = ChatAdapter(messages)
        binding.rvMessages.layoutManager = LinearLayoutManager(this)
        binding.rvMessages.adapter = adapter

        binding.tvBack.setOnClickListener { finish() }

        ajouter(
            ChatMessageDto(
                role = "bot",
                text = "Bonjour. Décrivez ce que vous ressentez avec vos mots, je vous poserai quelques questions."
            )
        )

        binding.btnSend.setOnClickListener {
            val texte = binding.etMessage.text.toString().trim()
            if (texte.isNotEmpty()) {
                binding.etMessage.text.clear()
                envoyer(texte)
            }
        }

        binding.btnContinuer.setOnClickListener {
            binding.panelReco.visibility = View.GONE
            binding.inputBar.visibility = View.VISIBLE
        }

        binding.btnVoirMedecins.setOnClickListener {
            ChatbotContext.resume = resumeChatbot
            ChatbotContext.specialite = specialiteChoisie

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("open_search", true)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun envoyer(texte: String) {
        binding.chipGroup.removeAllViews()
        binding.chipGroup.visibility = View.GONE
        ajouter(ChatMessageDto("user", texte))
        binding.tvTyping.visibility = View.VISIBLE
        binding.btnSend.isEnabled = false

        api.envoyer(ChatRequestDto(messages.toList()))
            .enqueue(object : Callback<ChatResponseDto> {
                override fun onResponse(
                    call: Call<ChatResponseDto>,
                    response: Response<ChatResponseDto>
                ) {
                    binding.tvTyping.visibility = View.GONE
                    binding.btnSend.isEnabled = true
                    val r = response.body()
                    if (!response.isSuccessful || r == null) {
                        ajouter(ChatMessageDto("bot", "Erreur ${response.code()}. Réessayez."))
                        return
                    }
                    ajouter(ChatMessageDto("bot", r.reponse ?: "..."))

                    val specs = r.specialites.orEmpty()
                    if (r.termine && specs.isNotEmpty()) {
                        resumeChatbot = r.resume?.trim()?.takeIf { it.isNotEmpty() }
                        afficherReco(specs)
                    } else {
                        afficherReponsesRapides(r.reponsesRapides.orEmpty())
                    }
                }

                override fun onFailure(call: Call<ChatResponseDto>, t: Throwable) {
                    binding.tvTyping.visibility = View.GONE
                    binding.btnSend.isEnabled = true
                    ajouter(ChatMessageDto("bot", "Connexion impossible. Vérifiez votre réseau."))
                }
            })
    }

    private fun afficherReponsesRapides(reponses: List<String>) {
        binding.chipGroup.removeAllViews()
        if (reponses.isEmpty()) return
        val dp = resources.displayMetrics.density
        for (texte in reponses) {
            val chip = TextView(this).apply {
                text = texte
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(ContextCompat.getColor(context, R.color.teal))
                setBackgroundResource(R.drawable.chip_bg)
                setPadding(
                    (18 * dp).toInt(),
                    (12 * dp).toInt(),
                    (18 * dp).toInt(),
                    (12 * dp).toInt()
                )
                isClickable = true
                isFocusable = true
                setOnClickListener { envoyer(texte) }
            }
            binding.chipGroup.addView(chip)
        }
        binding.chipGroup.visibility = View.VISIBLE
    }

    private fun afficherReco(specs: List<String>) {
        specialiteChoisie = specs[0]
        binding.tvSpec.text = specs[0]
        if (specs.size > 1) {
            binding.tvAutre.text = "Peut aussi convenir : ${specs[1]}"
            binding.tvAutre.visibility = View.VISIBLE
        } else {
            binding.tvAutre.visibility = View.GONE
        }
        binding.inputBar.visibility = View.GONE
        binding.chipGroup.visibility = View.GONE
        binding.panelReco.visibility = View.VISIBLE
    }

    private fun ajouter(m: ChatMessageDto) {
        messages.add(m)
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)
    }
}