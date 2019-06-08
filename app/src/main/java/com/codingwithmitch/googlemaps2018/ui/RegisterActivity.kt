package com.codingwithmitch.googlemaps2018.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.models.User
import com.codingwithmitch.googlemaps2018.util.doStringMatch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    // Variables
    private var mDb: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register.setOnClickListener(this)

        mDb = FirebaseFirestore.getInstance()
        hideSoftKeyboard()
    }

    /**
     * Register a new email and password to Firebase Authentication.
     * @param email
     * @param password
     */
    fun registerNewEmail(email: String, password: String) {
        showDialog()

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { createUserTask ->
                    Log.d(TAG, "createUserWithEmail:onComplete: ${createUserTask.isSuccessful}")

                    if (createUserTask.isSuccessful) {
                        Log.d(TAG, "onComplete: AuthState: ${FirebaseAuth.getInstance().currentUser?.uid}")

                        // Insert some default data.
                        val user = User()
                        user.email = email
                        user.username = email.substring(0, email.indexOf("@"))
                        user.user_id = FirebaseAuth.getInstance().uid

                        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
                        mDb?.firestoreSettings = settings

                        val newUserRef = mDb?.collection("Users")?.document(FirebaseAuth.getInstance().uid!!)
                        newUserRef?.set(user)?.addOnCompleteListener { setUserInfoTask ->
                            if (setUserInfoTask.isSuccessful) {
                                redirectLoginScreen()

                            } else {
                                Snackbar.make(
                                        findViewById(android.R.id.content),
                                        "Something went wrong.",
                                        Snackbar.LENGTH_SHORT
                                ).show()
                                hideDialog()
                            }
                        }
                    }
                }
    }

    private fun redirectLoginScreen() {
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_register -> {
                Log.d(TAG, "onClick: attempting to register.")

                // Check for null valued EditText fields.
                if (!isEmpty(input_email.text.toString())
                        && !isEmpty(input_password.text.toString())
                        && !isEmpty(input_confirm_password.text.toString())) {

                    // Check if passwords match.
                    if (doStringMatch(input_password.text.toString(), input_confirm_password.text.toString())) {
                        // Initiate registration task.
                        registerNewEmail(input_email.text.toString(), input_password.text.toString())
                    }
                    else {
                        Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "You must fill out all the fields.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}