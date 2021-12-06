package com.example.taskyourtime.group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskyourtime.R
import com.example.taskyourtime.databinding.ActivityVisualizeGroupBinding
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.model.Publication
import com.example.taskyourtime.services.GroupService
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class VisualizeGroupActivity : AppCompatActivity() {
    private var binding: ActivityVisualizeGroupBinding? = null
    private val groupService by inject<GroupService>()
    private lateinit var group: Group

    private val TAG = "VisualizeGroupActivity"
    private lateinit var database: DatabaseReference

    private val publications: MutableList<Publication> = mutableListOf<Publication>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizeGroupBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        group = intent.getSerializableExtra("groupClicked") as Group

        binding!!.writePublication.setOnClickListener{
            /*val publicationIntent = Intent(this, CreatePublicationActivity::class.java)
            startActivity(publicationIntent)*/
        }

        database = Firebase.database.reference.child("publications")
        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val map = snapshot.value as Map<String?, Any?>
                val maPublication = Publication(map)
                Log.d(TAG, "ON PASSE ICI EN TOUT CAS : ${group.id}")
                if(maPublication.groupId == group.id){
                    maPublication.id = snapshot.key!!
                    maPublication.title?.let { Log.d(TAG, it) }
                    publications.add(maPublication)
                    binding!!.recyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val index = publications.indexOfFirst {it.id == snapshot.key!! } // -1 if not found
                if (index >= 0){
                    val map = snapshot.value as Map<String?, Any?>
                    val maPublication = Publication(map)
                    val pubId = publications[index].id
                    publications[index] = maPublication
                    publications[index] = maPublication
                    publications[index].id = pubId
                    binding!!.recyclerView.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "OnChildRemoved : " + snapshot.key!!)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "OnChildMoved : " + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "OnCancelled : " + error.toException())
                Toast.makeText(applicationContext, "Failed to load publications", Toast.LENGTH_SHORT).show()
            }
        }
        database.addChildEventListener(childEventListener)
        displayPublications()
    }

    private fun displayPublications(){
        binding!!.recyclerView.layoutManager = LinearLayoutManager(this)
        binding!!.recyclerView.adapter = PublicationPostAdapter(publications, groupService)
        binding!!.loaderFeed.isVisible = false
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}