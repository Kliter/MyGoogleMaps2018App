package com.codingwithmitch.googlemaps2018.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.Chatroom

class ChatroomRecyclerAdapter(
        chatrooms: MutableList<Chatroom>, chatroomRecyclerClickListener: ChatroomRecyclerClickListener
): RecyclerView.Adapter<ChatroomRecyclerAdapter.ViewHolder>() {

    interface ChatroomRecyclerClickListener {
        fun onChatroomSelected(position: Int)
    }

    class ViewHolder(view: View, clickListener: ChatroomRecyclerClickListener):
            RecyclerView.ViewHolder(view), View.OnClickListener {

        var chatroomTitle: TextView? = null
        var clickListener: ChatroomRecyclerClickListener? = null

        init {
            chatroomTitle = view.findViewById(R.id.chatroom_title)
            this.clickListener = clickListener
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener?.onChatroomSelected(adapterPosition)
        }
    }

    private var mChatrooms: MutableList<Chatroom> = mutableListOf()
    private var mChatroomRecyclerClickListener: ChatroomRecyclerClickListener? = null

    init {
        mChatrooms = chatrooms
        mChatroomRecyclerClickListener = chatroomRecyclerClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_chatroom_list_item, parent, false)
        return ViewHolder(view, mChatroomRecyclerClickListener!!)
    }

    override fun getItemCount(): Int {
        return mChatrooms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.chatroomTitle?.text = mChatrooms[position].title
    }
}