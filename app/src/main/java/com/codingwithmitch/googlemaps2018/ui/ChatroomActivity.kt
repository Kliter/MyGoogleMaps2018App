package com.codingwithmitch.googlemaps2018.ui

import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import com.codingwithmitch.googlemaps2018.R
import com.codingwithmitch.googlemaps2018.UserClient
import com.codingwithmitch.googlemaps2018.adapters.ChatMessageRecyclerAdapter
import com.codingwithmitch.googlemaps2018.models.ChatMessage
import com.codingwithmitch.googlemaps2018.models.Chatroom
import com.codingwithmitch.googlemaps2018.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_chatroom.*
import java.util.ArrayList

class ChatroomActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val TAG = "ChatroomActivity"
    }

    // Widgets
    var mChatroom: Chatroom? = null

    // Variables
    private var mChatMessageEventListener: ListenerRegistration? = null
    private var mUserListEventListener: ListenerRegistration? = null
    private var mChatMessageRecyclerAdapter: ChatMessageRecyclerAdapter? = null
    private var mDb: FirebaseFirestore? = null
    private var mMessages: MutableList<ChatMessage> = mutableListOf()
    private var mMessageIds: MutableSet<String> = mutableSetOf()
    private var mUserList: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        checkmark.setOnClickListener(this)

        mDb = FirebaseFirestore.getInstance()
        getIncomingIntent()
        initChatroomRecyclerView()
        getChatroomUsers()
    }

    private fun getChatMessages() {
        val messagesRef: CollectionReference = mDb
                ?.collection(getString(R.string.collection_chatrooms))
                ?.document(mChatroom?.chatroom_id!!)
                ?.collection(getString(R.string.collection_chat_messages))!!

        mChatMessageEventListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { queryDocumentSnapshots, error ->
                    if (error != null) {
                        Log.e(TAG, "onEvent: Listen failed.", error)
                    }

                    if (queryDocumentSnapshots != null) {
                        for (doc: QueryDocumentSnapshot in queryDocumentSnapshots) {
                            val message: ChatMessage = doc.toObject(ChatMessage::class.java)
                            if (!mMessageIds.contains(message.message_id)) {
                                mMessageIds.add(message.message_id!!)
                                mMessages.add(message)
                                chatmessage_recycler_view.smoothScrollToPosition(mMessages.size - 1)
                            }
                        }
                        mChatMessageRecyclerAdapter.let {
                            it?.notifyDataSetChanged()
                        }
                    }
                }
    }

    private fun getChatroomUsers() {
        val userRef: CollectionReference = mDb
                ?.collection(getString(R.string.collection_chatrooms))
                ?.document(mChatroom?.chatroom_id!!)
                ?.collection(getString(R.string.collection_chatroom_user_list))!!

        mUserListEventListener = userRef.addSnapshotListener { queryDocumentSnapshots, error ->
            if (error != null) {
                Log.e(TAG, "onEvent: Listen failed.", error)
                return@addSnapshotListener
            }

            if (queryDocumentSnapshots != null) {
                // Clear the list and add all the users again.
                mUserList.clear()
                mUserList = mutableListOf()

                for (doc: QueryDocumentSnapshot in queryDocumentSnapshots) {
                    val user: User = doc.toObject(User::class.java)
                    mUserList.add(user)
                }

                Log.d(TAG, "onEvent: userlist size: ${mUserList.size}")
            }
        }
    }

    private fun initChatroomRecyclerView() {
        mChatMessageRecyclerAdapter = ChatMessageRecyclerAdapter(mMessages, mutableListOf<User>(), this)
        chatmessage_recycler_view.adapter = mChatMessageRecyclerAdapter
        chatmessage_recycler_view.layoutManager = LinearLayoutManager(this)

        chatmessage_recycler_view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                chatmessage_recycler_view.postDelayed({
                    if (mMessages.size > 0) {
                        chatmessage_recycler_view.smoothScrollToPosition(
                                chatmessage_recycler_view.adapter?.itemCount!! - 1
                        )
                    }
                }, 100)

            }
        }
    }

    private fun insertNewMessage() {
        var message: String = input_message.text.toString()

        if (message != "") {
            message = message.replace(System.getProperty("line.separator")!!.toRegex(), "")
            val newMessageDoc: DocumentReference = mDb
                    ?.collection(getString(R.string.collection_chatrooms))
                    ?.document(mChatroom?.chatroom_id!!)
                    ?.collection(getString(R.string.collection_chat_messages))?.document()!!

            val newChatMessage = ChatMessage()
            newChatMessage.message = message
            newChatMessage.message_id = newMessageDoc.id

            val user: User = (applicationContext as UserClient).user!!
            Log.d(TAG, "insertNewMessage: retrieved user client: $user")
            newChatMessage.user = user

            newMessageDoc.set(newChatMessage).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    clearMessage()
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Something went wrong.", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearMessage() {
        input_message.setText("")
    }

    private fun inflateUserListFragment() {
        hideSoftKeyboard()

        val fragment: UserListFragment = UserListFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelableArrayList(getString(R.string.intent_user_list), (mUserList as ArrayList))
        fragment.arguments = bundle

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
        transaction.replace(R.id.user_list_container, fragment, getString(R.string.fragment_user_list))
        transaction.commit()
    }

    private fun hideSoftKeyboard() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    private fun getIncomingIntent() {
        if (intent.hasExtra(getString(R.string.intent_chatroom))) {
            mChatroom = intent.getParcelableExtra(getString(R.string.intent_chatroom))
            setChatroomName()
            joinChatroom()
        }
    }

    private fun leaveChatroom() {
        var joinChatroomRef: DocumentReference? = null
        if (mDb != null) {
            joinChatroomRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom?.chatroom_id!!)
                    .collection(getString(R.string.collection_chatroom_user_list))
                    .document(FirebaseAuth.getInstance().uid!!)
        }

        joinChatroomRef?.delete()
    }

    private fun joinChatroom() {
        val joinChatroomRef: DocumentReference?
        if (mDb != null) {
            joinChatroomRef = mDb!!
                    .collection(getString(R.string.collection_chatrooms))
                    .document(mChatroom?.chatroom_id!!)
                    .collection(getString(R.string.collection_chatroom_user_list))
                    .document(FirebaseAuth.getInstance().uid!!)

            val user: User = (applicationContext as UserClient).user!!
            joinChatroomRef.set(user)
        }
    }

    private fun setChatroomName() {
        supportActionBar?.title = mChatroom?.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        getChatMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatMessageEventListener?.remove()
        mUserListEventListener?.remove()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatroom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                val fragment: UserListFragment =
                        (supportFragmentManager.findFragmentByTag(getString(R.string.fragment_user_list)) as UserListFragment)
                if (fragment.isVisible) {
                    supportFragmentManager.popBackStack()
                    return true
                }

                finish()
                return true
            }

            R.id.action_chatroom_user_list -> {
                inflateUserListFragment()
                return true
            }

            R.id.action_chatroom_leave -> {
                leaveChatroom()
                return true
            }

            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.checkmark -> {
                insertNewMessage()
            }
        }
    }
}