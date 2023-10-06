package com.example.orientconnect.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orientconnect.AdapterClasses.UserAdapter
import com.example.orientconnect.ModelClasses.Users
import com.example.orientconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUserET)

        mUsers = ArrayList()
        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not implemented
            }

            override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {
                // Not implemented
            }
        })

        return view
    }

    private fun retrieveAllUsers() {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString() == "") {
                    for (snapshot in dataSnapshot.children) {
                        val user: Users? = snapshot.getValue(Users::class.java)
                        if (user != null && user.getUID() != firebaseUserID) {
                            (mUsers as ArrayList<Users>).add(user)
                        }
                    }
                    userAdapter = UserAdapter(context!!, mUsers!!, false)
                    recyclerView!!.adapter = userAdapter
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Not implemented
            }
        })
    }

    private fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUsers as ArrayList<Users>).clear()
                for (snapshot in dataSnapshot.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (user != null && user.getUID() != firebaseUserID) {
                        (mUsers as ArrayList<Users>).add(user)
                    }
                }
                userAdapter = UserAdapter(context!!, mUsers!!, false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Not implemented
            }
        })
    }
}
