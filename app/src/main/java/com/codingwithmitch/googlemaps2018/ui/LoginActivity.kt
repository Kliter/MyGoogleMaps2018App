package com.codingwithmitch.googlemaps2018.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.codingwithmitch.googlemaps2018.UserClient
import com.codingwithmitch.googlemaps2018.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "LoginActivity"
    }

    // Firebase
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {

    }

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user: FirebaseUser? = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged: signed in: ${user.email}")
                Toast.makeText(this, "Authenticated with ${user.email}", Toast.LENGTH_SHORT).show()

                val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
                db.firestoreSettings = settings

                val userRef: DocumentReference = db.collection("Users").document(user.uid)
                userRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "onComplete: successfully set the user client.")
                        val user: User? = task.result.let {
                            it?.toObject(User::class.java)
                        }Goo
                        (applicationContext as UserClient).user = user
                    }
                }

                val intent: Intent = Intent(this, MainActivity::class.java)
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
        }
    }
}
