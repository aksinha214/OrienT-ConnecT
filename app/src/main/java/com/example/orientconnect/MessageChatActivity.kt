package com.example.orientconnect

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orientconnect.AdapterClasses.ChatsAdapter
import com.example.orientconnect.ModelClasses.Chat
import com.example.orientconnect.ModelClasses.Users
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.storage.StorageReference

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var send_message_btn: ImageView? = null
    var username_mchat: TextView? = null
    private lateinit var text_message: EditText
    private lateinit var profile_image_mchat: CircleImageView
    private lateinit var attact_image_file_btn: ImageView
    private var reference: DatabaseReference? = null
    private var chatsListReference: DatabaseReference? = null
    private lateinit var recycler_view_chats: RecyclerView
    private  var toolbar_message_chat:Toolbar?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@MessageChatActivity,WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id")!!
        firebaseUser = FirebaseAuth.getInstance().currentUser

        recycler_view_chats = findViewById(R.id.recycler_view_chats)
        send_message_btn = findViewById(R.id.send_message_btn)
        text_message = findViewById(R.id.text_message)
        username_mchat = findViewById(R.id.username_mchat)
        profile_image_mchat = findViewById(R.id.profile_image_mchat)
        attact_image_file_btn = findViewById(R.id.attact_image_file_btn)


        recycler_view_chats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        recycler_view_chats.layoutManager = linearLayoutManager
        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user: Users? = p0.getValue(Users::class.java)
                username_mchat!!.text = user!!.getUserName()
                Picasso.get().load(user.getProfile()).into(profile_image_mchat)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        send_message_btn!!.setOnClickListener {
            val message = text_message.text.toString()
            if (message == "") {
                Toast.makeText(this@MessageChatActivity, "Please write a message, first...", Toast.LENGTH_LONG).show()
            } else {
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }
            text_message.setText("")
        }

        attact_image_file_btn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val messageKey = reference!!.push().key
        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference!!.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                chatsListReference!!.child("id").setValue(userIdVisit)
                            }

                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)

                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)
                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }
                    })
                }
            }
    }

    private fun retrieveMessages(senderId: String, receiverId: String?, receiverImageUrl: String?) {
        val mChatList = ArrayList<Chat>()
        reference = FirebaseDatabase.getInstance().reference.child("Chats")

    reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                mChatList.clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat != null && (chat.getReceiver() == senderId && chat.getSender() == receiverId
                                || chat.getReceiver() == receiverId && chat.getSender() == senderId)) {
                        mChatList.add(chat)
                    }
                }
                val chatsAdapter = ChatsAdapter(this@MessageChatActivity, mChatList, receiverImageUrl!!)
                val recycler_view_chats=findViewById<RecyclerView>(R.id.recycler_view_chats)
                recycler_view_chats.adapter = chatsAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    var seenListner: ValueEventListener? = null
    private fun seenMessage(userId:String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListner = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
               for (dataSnapshot in p0.children)
               {
                   val chat = dataSnapshot.getValue(Chat::class.java)

                   if (chat!!.getReceiver().equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId)){
                       val hashMap = HashMap<String, Any>()
                       hashMap["isseen"] = true
                       dataSnapshot.ref.updateChildren(hashMap)

                   }

               }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListner!!)
    }
}
