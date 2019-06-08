package com.codingwithmitch.googlemaps2018.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.User

class UserRecyclerAdapter(users: MutableList<User>): RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var username: TextView? = null
        var email: TextView? = null

        init {
            username = view.findViewById(R.id.username)
            email = view.findViewById(R.id.email)
        }
    }

    private val mUsers: MutableList<User> = users

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_user_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.username?.text = mUsers[position].username
        holder.email?.text = mUsers[position].email
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }
}