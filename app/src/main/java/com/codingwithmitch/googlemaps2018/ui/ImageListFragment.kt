package com.codingwithmitch.googlemaps2018.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.ImageListRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_image_list.*

class ImageListFragment: Fragment(), ImageListRecyclerAdapter.ImageListRecyclerClickListener {

    companion object {
        private const val TAG = "ImageListFragment"
        private const val NUM_COLUMNS = 2;

        fun newInstance(): ImageListFragment {
            return ImageListFragment()
        }
    }

    //Variables
    private var mImageResources: MutableList<Int> = mutableListOf(
            R.drawable.cwm_logo,
            R.drawable.cartman_cop,
            R.drawable.eric_cartman,
            R.drawable.ike,
            R.drawable.kyle,
            R.drawable.satan,
            R.drawable.chef,
            R.drawable.tweek
    )

    private var mIProfile: IProfile? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initRecyclerView()
        return  inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    private fun initRecyclerView() {
        image_list_recyclerview?.adapter = ImageListRecyclerAdapter(context!!, mImageResources, this)
        image_list_recyclerview?.layoutManager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mIProfile = activity as IProfile
    }

    override fun onImageSelected(position: Int) {
        mIProfile?.onImageSelected(mImageResources[position])
    }
}