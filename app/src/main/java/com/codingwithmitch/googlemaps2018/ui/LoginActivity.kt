package com.codingwithmitch.googlemaps2018.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.UserClient
import com.codingwithmitch.googlemaps2018.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view.*

class LoginActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "LoginActivity"
    }

    // Firebase
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupFirebaseAuth()
        email_sign_in_button.setOnClickListener(this)
        link_register.setOnClickListener(this)

        hideSoftKeyboard()
    }

    private fun showDialog() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideDialog() {
        if (progressBar.visibility == View.VISIBLE) {
            progressBar.visibility = View.INVISIBLE
        }
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val authenticatedUser: FirebaseUser? = firebaseAuth.currentUser
            if (authenticatedUser != null) {
                Log.d(TAG, "onAuthStateChanged: signed in: ${authenticatedUser.email}")
                Toast.makeText(this, "Authenticated with ${authenticatedUser.email}", Toast.LENGTH_SHORT).show()

                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
                db.firestoreSettings = settings

                val userRef: DocumentReference = db.collection("Users").document(authenticatedUser.uid)
                userRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: successfully set the user client.")
                        val user: User? = task.result?.toObject(User::class.java)
                        (applicationContext as UserClient).user = user
                    }
                }

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            else {
                // User is signed out.
                Log.d(TAG, "onAuthStateChanged: signed out.")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }

    }

    private fun signIn() {
        // Check if the fields are filled out.
        if (!isEmpty(email.text.toString()) && !isEmpty(password.text.toString())) {
            Log.d(TAG, "onClick: attempting to authenticate.")

            showDialog()

            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                            email.text.toString(),
                            password.text.toString()
                    )
                    .addOnCompleteListener {
                        hideDialog()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                        hideDialog()
                    }
        } else {
            Toast.makeText(this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.link_register -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.email_sign_in_button -> {
                signIn()
            }
        }
    }
}
