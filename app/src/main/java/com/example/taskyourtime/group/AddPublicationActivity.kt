package com.example.taskyourtime.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.databinding.ActivityAddPublicationBinding
import com.example.taskyourtime.model.*
import com.example.taskyourtime.services.GroupService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class AddPublicationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPublicationBinding
    private val groupService by inject<GroupService>()
    private val TAG = "AddPublicationActivity"
    private var database: DatabaseReference = Firebase.database.reference
    private var database2: DatabaseReference = Firebase.database.reference
    private var database3: DatabaseReference = Firebase.database.reference

    private val choices : MutableList<Choice> = mutableListOf<Choice>()

    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPublicationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        group = intent.getSerializableExtra("group") as Group

        binding.buttonCancel.setOnClickListener{
            finish()
        }

        binding.buttonPublish.setOnClickListener{
            var theChoice: Choice? = null
            choices.forEach{
                if(it.checked == true){
                    theChoice = it
                }
            }
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            groupService.createPublication(
                binding.publicationTitle.text.toString(),
                group.id.toString(),
                Firebase.auth.uid.toString(),
                theChoice?.type.toString(),
                theChoice?.id.toString(),
                theChoice?.id.toString(),
                theChoice?.id.toString(),
                currentDate.toString(),
            )
            Log.d(TAG, "le choix : ${theChoice.toString()}")
            finish()

        }

        displayChoices()

        database = Firebase.database.reference.child("notes")
        val noteChildEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val maNote = Note(map)
                maNote.id = snapshot.key!!
                maNote.name?.let { Log.d(TAG, it) }
                if(Firebase.auth.uid == maNote.user_id){
                    val noteChoice = Choice(maNote.id.toString(), maNote.name, maNote.content, null, null, false, "NOTE")
                    choices.add(noteChoice)
                }
                binding.recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", error.toException())
            }
        }
        database.addChildEventListener(noteChildEventListener)
    }

    private fun displayChoices(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = ChoiceAdapter(choices, groupService, this)
        binding.loaderFeed.isVisible = false
    }
}