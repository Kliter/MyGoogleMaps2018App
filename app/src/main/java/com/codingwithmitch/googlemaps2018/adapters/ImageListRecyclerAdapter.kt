package com.codingwithmitch.googlemaps2018.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.googlemaps2018.R

class ImageListRecyclerAdapter(
        context: Context,
        images: MutableList<Int>,
        imageListRecyclerClickListener: ImageListRecyclerClickListener
) : RecyclerView.Adapter<ImageListRecyclerAdapter.ViewHolder>() {

    interface ImageListRecyclerClickListener {
        fun onImageSelected(position: Int)
    }

    class ViewHolder(view: View, clickListener: ImageListRecyclerClickListener)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        var image: ImageView? = view.findViewById(R.id.image)
        private var mClickListener: ImageListRecyclerClickListener? = clickListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mClickListener?.onImageSelected(adapterPosition)
        }
    }

    private var mImages = images
    private var mImageListRecyclerClickListener: ImageListRecyclerClickListener? = imageListRecyclerClickListener
    private var mContext: Context? = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_image_list_item, parent, false)
        return ViewHolder(view, mImageListRecyclerClickListener!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestOptions: RequestOptions =
                RequestOptions()
                .placeholder(R.drawable.cwm_logo)
                .error(R.drawable.cwm_logo)

        Glide.with(mContext!!)
                .setDefaultRequestOptions(requestOptions)
                .load(mImages[position])
                .into(holder.image!!)
    }

    override fun getItemCount(): Int {
        return mImages.size
    }
}