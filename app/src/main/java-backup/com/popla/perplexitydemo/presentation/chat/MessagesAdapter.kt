package com.popla.perplexitydemo.presentation.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.popla.perplexitydemo.R
import com.popla.perplexitydemo.data.model.Message
import com.popla.perplexitydemo.data.model.MessageRole
import java.time.format.DateTimeFormatter

class MessagesAdapter : ListAdapter<Message, MessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutId = when (viewType) {
            MessageRole.USER.ordinal -> R.layout.item_user_message
            MessageRole.ASSISTANT.ordinal -> R.layout.item_ai_message
            else -> R.layout.item_system_message
        }
        
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun getItemViewType(position: Int): Int {
        return getItem(position).role.ordinal
    }
    
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textContent: TextView = itemView.findViewById(R.id.textContent)
        private val textTimestamp: TextView = itemView.findViewById(R.id.textTimestamp)
        
        fun bind(message: Message) {
            textContent.text = message.content
            textTimestamp.text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))
            
            if (message.hasError) {
                textContent.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }
        }
    }
    
    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}