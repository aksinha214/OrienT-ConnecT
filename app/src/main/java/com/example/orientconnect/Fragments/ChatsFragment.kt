package com.example.orientconnect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orientconnect.AdapterClasses.UserAdapter
import com.example.orientconnect.ModelClasses.Chatlist
import com.example.orientconnect.ModelClasses.Users
import com.example.orientconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<Chatlist>? = null
    lateinit var recycler_view_chatlist: RecyclerView
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chatlist = view.findViewById(R.id.recycler_view_chatlist)
        recycler_view_chatlist.setHasFixedSize(true)
        recycler_view_chatlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (dataSnapshot in dataSnapshot.children) {
                    val chatlist = dataSnapshot.getValue(Chatlist::class.java)
                    (usersChatList as ArrayList).add(chatlist!!)
                }
                retrieveChatList()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        return view
    }

    private fun retrieveChatList() {
        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUsers as ArrayList).clear()

                for (dataSnapshot in dataSnapshot.children) {
                    val user = dataSnapshot.getValue(Users::class.java)

                    for (eachChatList in usersChatList!!) {
                        if (user!!.getUID() == eachChatList.getId()) {
                            (mUsers as ArrayList).add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, (mUsers as ArrayList<Users>), true)
                recycler_view_chatlist.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
