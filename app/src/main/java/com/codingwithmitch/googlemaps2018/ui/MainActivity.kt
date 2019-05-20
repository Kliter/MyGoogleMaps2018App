package com.codingwithmitch.googlemaps2018.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.adapters.ChatroomRecyclerAdapter
import com.codingwithmitch.googlemaps2018.models.Chatroom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity(), ChatroomRecyclerAdapter.ChatroomRecyclerClickListener {

    companion object {
        private const val TAG = "MainActivity"
    }

    // Variables
    private val mChatrooms: MutableList<Chatroom> = mutableListOf()
    private val mChatroomIds: MutableSet<String> = mutableSetOf()
    private var mChatroomRecyclerAdapter: ChatroomRecyclerAdapter? = null
    private var mChatroomEventListener: ListenerRegistration? = null
    private var mDb: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_create_chatroom.setOnClickListener { view ->
            when (view.id) {
                R.id.fab_create_chatroom -> {
                    newChatroomDialog()
                }
            }
        }

        mDb = FirebaseFirestore.getInstance()
        initSupportActionBar()
        initChatroomRecyclerView()
    }

    private fun initSupportActionBar() {
        title = getString(R.string.chatroom_actionbar_title)
    }

    private fun initChatroomRecyclerView() {
        mChatroomRecyclerAdapter = ChatroomRecyclerAdapter(mChatrooms, this)
        chatrooms_recycler_view.adapter = mChatroomRecyclerAdapter
        chatrooms_recycler_view.layoutManager = LinearLayoutManager(this)
    }

    private fun getChatrooms() {
        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
        mDb.let {
            it?.firestoreSettings = settings
        }

        // Get collection of chatroom from Firebase.
        val chatroomsCollection: CollectionReference = mDb?.collection(getString(R.string.collection_chatrooms))!!
        mChatroomEventListener = chatroomsCollection.addSnapshotListener { queryDocumentSnapshots, error ->
            // Fired when the data is added into Firebase storage.
            Log.d(TAG, "onEvent: called.", error)

            if (error != null) {
                Log.e(TAG, "onEvent: Listen failed.", error)
            }

            if (queryDocumentSnapshots != null) {
                for (doc: QueryDocumentSnapshot in queryDocumentSnapshots) {
                    val chatroom: Chatroom = doc.toObject(Chatroom::class.java)
                    if (!mChatroomIds.contains(chatroom.chatroom_id)) {
                        mChatroomIds.add(chatroom.chatroom_id)
                        mChatrooms.add(chatroom)
                    }
                }

                Log.d(TAG, "onEvent: number of chatrooms: " + mChatrooms.size)
                mChatroomRecyclerAdapter.let {
                    it?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun navChatroomActivity(chatroom: Chatroom) {
        val intent = Intent(this, ChatroomActivity::class.java)
        intent.putExtra(getString(R.string.intent_chatroom), chatroom)
        startActivity(intent)
    }

    private fun newChatroomDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.chatroom_dialog_title))

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(getString(R.string.chatroom_dialog_title_positive_button)) { _, _ ->
            if (input.text.toString() != "") {
                buildNewChatroom(input.text.toString())
            }
            else {
                Toast.makeText(this, getString(R.string.chatroom_dialog_title), Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton(getString(R.string.chatroom_dialog_title_negative_button)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun buildNewChatroom(chatroomName: String) {
        val chatroom= Chatroom()
        chatroom.title = chatroomName

        val settings: FirebaseFirestoreSettings = FirebaseFirestoreSettings.Builder().build()
        mDb.let {
            it?.firestoreSettings = settings
        }

        val newChatroomRef: DocumentReference = mDb.let {
            it?.collection(getString(R.string.collection_chatrooms))?.document()
        }!!
        chatroom.chatroom_id = newChatroomRef.id
        newChatroomRef.set(chatroom).addOnCompleteListener { task ->
            hideDialog()

            if (task.isSuccessful) {
                navChatroomActivity(chatroom)
            }
            else {
                val parentLayout: View = findViewById(R.id.content)
                Snackbar.make(parentLayout, "Something went wrong.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onChatroomSelected(position: Int) {
        navChatroomActivity(mChatrooms[position])
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                true
            }
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatroomEventListener.let {
            it?.remove()
        }
    }

    override fun onResume() {
        super.onResume()
        getChatrooms()
    }

    private fun hideDialog() {
        progressBar.let {
            it?.visibility = View.GONE
        }
    }
}