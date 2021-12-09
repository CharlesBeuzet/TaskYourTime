package com.example.taskyourtime.group

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.databinding.ActivityListGroupBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.services.GroupService
import com.example.taskyourtime.services.UserService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject


class ListGroupActivity : Fragment(), ListGroupAdapter.OnItemClickListener{

    private var binding : ActivityListGroupBinding? = null
    private val groupService by inject<GroupService>()
    private val _binding get() = binding!!

    private val TAG = "ListGroupActivity"
    private lateinit var database: DatabaseReference
    private val groups: MutableList<Group> = mutableListOf()
    private val userService by inject<UserService>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityListGroupBinding.inflate(inflater, container, false)
        return _binding.root
    }

    private fun displayGroups(){
        Log.d(TAG, "displayGroups")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = ListGroupAdapter(groups, groupService,userService, this, context)
        binding?.loaderFeed?.isVisible = false
    }

    override fun onStop(){
        super.onStop()
        Log.i(TAG, "ACTIVITY STOPPED")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "ACTIVITY DESTROYED")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"Fragment started")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "ListGroupView created")
        super.onViewCreated(view, savedInstanceState)
        Log.d("Group creation", "Inside the createview of groups")
        binding?.addGroupButton?.setOnClickListener{
            Log.d("Click", "Adding group")
            val intentAddGroup = Intent(context, AddGrpActivity::class.java)
            startActivity(intentAddGroup)
        }

        displayGroups()

        database = Firebase.database.reference.child("groups")
        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val myGroup = Group(map)
                val userIdList = map["userIdList"]
                myGroup.userIdList = userIdList as HashMap<String?, Any?>
                myGroup.id = snapshot.key!!
                myGroup.name?.let { Log.d(TAG, it) }
                if(Firebase.auth.uid == myGroup.ownerId || myGroup.userIdList.contains(Firebase.auth.uid)){
                    groups.add(myGroup)
                }
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val index = groups.indexOfFirst {it.id == snapshot.key!!}
                val checkMap = snapshot.value as Map<String?, Any?>
                val users = checkMap["userIdList"]
                val checkGroup = Group(checkMap)
                checkGroup.userIdList = users as HashMap<String?, Any?>
                if(index >= 0 && (groups[index].ownerId == Firebase.auth.uid || groups[index].userIdList.contains(Firebase.auth.uid))){
                    val map = snapshot.value as Map<String?, Any?>
                    val myGroup = Group(map)
                    val groupId = groups[index].id
                    groups[index] = myGroup
                    groups[index].id = groupId
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                    //binding?.recyclerView?.adapter?.notifyItemChanged(index)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)
                val index = groups.indexOfFirst { it.id == snapshot.key!! }
                if(index >= 0 && (groups[index].ownerId == Firebase.auth.uid || groups[index].userIdList.contains(Firebase.auth.uid))){
                    val map = snapshot.value as Map<String?, Any?>
                    groups.removeAt(index)
                    binding!!.recyclerView.adapter!!.notifyItemRemoved(index)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "onCancelled", error.toException())
            }
        }
        database.addChildEventListener(childEventListener)
    }

    override fun onItemClick(position: Int){
        if(groups[position].userIdList[Firebase.auth.uid] == "yes" || groups[position].ownerId == Firebase.auth.uid){
            val intentVisualizeGroup = Intent(context, VisualizeGroupActivity::class.java)
            intentVisualizeGroup.putExtra("groupClicked",groups[position])
            startActivity(intentVisualizeGroup)
        }else{
            Toast.makeText(context, "Please accept invitation before", Toast.LENGTH_SHORT).show()
        }

    }


}