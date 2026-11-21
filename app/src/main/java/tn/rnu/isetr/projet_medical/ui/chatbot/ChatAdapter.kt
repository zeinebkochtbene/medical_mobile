package tn.rnu.isetr.projet_medical.ui.chatbot

import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import tn.rnu.isetr.projet_medical.R
import tn.rnu.isetr.projet_medical.data.ChatMessageDto
import tn.rnu.isetr.projet_medical.databinding.ItemChatMessageBinding

class ChatAdapter(private val messages: List<ChatMessageDto>) :
    RecyclerView.Adapter<ChatAdapter.VH>() {

    class VH(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = messages[position]
        val ctx = holder.itemView.context
        val tv = holder.binding.tvMessage
        val params = tv.layoutParams as FrameLayout.LayoutParams

        if (m.role == "user") {
            params.gravity = Gravity.END
            tv.setBackgroundResource(R.drawable.bubble_user)
            tv.setTextColor(ContextCompat.getColor(ctx, android.R.color.white))
            tv.setTypeface(null, Typeface.BOLD)
        } else {
            params.gravity = Gravity.START
            tv.setBackgroundResource(R.drawable.bubble_bot)
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.ink))
            tv.setTypeface(null, Typeface.NORMAL)
        }

        tv.layoutParams = params
        tv.text = m.text
    }
}