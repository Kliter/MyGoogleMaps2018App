package com.codingwithmitch.googlemaps2018.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.UserClient
import com.codingwithmitch.googlemaps2018.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.NumberFormatException

class ProfileActivity: AppCompatActivity(), View.OnClickListener, IProfile {

    companion object {
        private const val TAG = "ProfileActivity"
    }

    // Variables
    private var mImageListFragment: ImageListFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        image_choose_avatar.setOnClickListener(this)
        text_choose_avatar.setOnClickListener(this)

        retrieveProfileImage()
    }

    private fun retrieveProfileImage() {
        val requestOptions: RequestOptions = RequestOptions()
                .error(R.drawable.cwm_logo)
                .placeholder(R.drawable.cwm_logo)


        var avatar = 0
        try {
            avatar = if ((applicationContext as UserClient).user?.avatar != null) {
                Integer.parseInt((applicationContext as UserClient).user?.avatar!!)
            } else {
                0
            }

        }
        catch (e: NumberFormatException) {
            Log.e(TAG, "retrieveProfileImage: no avatar image. Setting default. ${e.message}")
        }

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(avatar)
                .into(image_choose_avatar)
    }


    override fun onClick(v: View?) {
        mImageListFragment = ImageListFragment()
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up)
                .replace(R.id.fragment_container, mImageListFragment!!, getString(R.string.fragment_image_list))
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                finish()
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onImageSelected(resource: Int) {
        // Remove the image selector fragment.
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_in_up)
                .remove(mImageListFragment as Fragment)
                .commit()

        // Display the image.
        val requestOptions: RequestOptions =
                RequestOptions().placeholder(R.drawable.cwm_logo).error(R.drawable.cwm_logo)

        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(resource)
                .into(image_choose_avatar)

        // Update the client and database.
        val user: User = (applicationContext as UserClient).user!!
        user.avatar = resource.toString()

        FirebaseFirestore
                .getInstance()
                .collection(getString(R.string.collection_users))
                .document(FirebaseAuth.getInstance().uid!!)
                .set(user)
    }
}
