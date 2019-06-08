package com.codingwithmitch.googlemaps2018.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.ChatMessage
import com.codingwithmitch.googlemaps2018.models.User
import com.google.firebase.auth.FirebaseAuth

class ChatMessageRecyclerAdapter(messages: MutableList<ChatMessage>, users: MutableList<User>, context: Context):
        RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>() {

    private var mMessages: MutableList<ChatMessage> = messages
    private var mUsers: MutableList<User> = users
    private var mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_chat_message_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (FirebaseAuth.getInstance().uid == mMessages[position].user?.user_id) { // Commented user is myself.
            holder.username?.setTextColor(ContextCompat.getColor(mContext, R.color.green1))
        }
        else {
            holder.username?.setTextColor(ContextCompat.getColor(mContext, R.color.blue2))
        }

        holder.username?.text = mMessages[position].user?.username
        holder.message?.text = mMessages[position].message
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var message: TextView? = null
        var username: TextView? = null

        init {
            message = view.findViewById(R.id.chat_message_message)
            username = view.findViewById(R.id.chat_message_username)
        }
    }
}