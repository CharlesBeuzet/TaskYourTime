package com.example.taskyourtime.todolist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskyourtime.databinding.ActivityToDoListBinding
import com.example.taskyourtime.model.ToDoItem
import com.example.taskyourtime.services.ToDoItemService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.android.ext.android.inject

class ToDoListActivity : Fragment() {

    private var binding: ActivityToDoListBinding? = null
    private val items : MutableList<ToDoItem> = mutableListOf<ToDoItem>()
    private val itemService by inject<ToDoItemService>()

    private val _binding get() = binding!!

    private val TAG = "ToDoListActivity"
    private lateinit var database: DatabaseReference
    private var itemsMoved : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityToDoListBinding.inflate(inflater, container, false)
        val view = _binding.root
        return view
    }

    private fun displayItems(){
        Log.d(TAG,"displayItems")
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.adapter = ListItemAdapter(items, itemService, context)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(_binding?.recyclerView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "ToDoListView created")
        super.onViewCreated(view, savedInstanceState)
        //go to activity create an item
        binding?.addItemButton?.setOnClickListener {
            val intentAddItem =
                Intent(context,AddItemActivity::class.java) //TODO : change activity
            startActivity(intentAddItem)
        }

        database = Firebase.database.reference.child("ItemToDoList")

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded::" + snapshot.key!!)
                val map = snapshot.value as Map<String?, Any?>
                val myItem = ToDoItem(map)
                myItem.id = snapshot.key!!
                //faire ce qu'on veut faire : afficher la note dans le recyclerView
                myItem.content?.let { Log.d(TAG, it) }
                if (Firebase.auth.uid == myItem.user_id) {
                    items.add(myItem)
                }
                //Ordering items by their position
                items.sortBy { it.position }
                Log.d(TAG,"Number of items : " + items.size)
                Log.d(TAG,"Checkbox : " + myItem.done)
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged:" + snapshot.key!!)
                val index = items.indexOfFirst { it.id == snapshot.key!! } // -1 if not found
                if (index >= 0 && items[index].user_id == Firebase.auth.uid) {
                    val map = snapshot.value as Map<String?, Any?>
                    val myItem = ToDoItem(map)
                    val ItemId = items[index].id
                    items[index] = myItem
                    items[index].id = ItemId
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + snapshot.key!!)
                val index = items.indexOfFirst { it.id == snapshot.key!! } //-1 if not found
                if (index >= 0 && Firebase.auth.uid == items[index].user_id) {
                    val map = snapshot.value as Map<String?, Any?>
                    items.removeAt(index)
                    binding?.recyclerView?.adapter?.notifyItemRemoved(index)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + snapshot.key!!)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", error.toException())
            }

        }
        database.addChildEventListener(childEventListener)

        displayItems()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"OnStop")
        if(itemsMoved) {
            //get the new position of the items and updating their position in bdd
            for(item in items) {
                Log.d(TAG,item.content.toString() + " "+ items.indexOf(item))
                item.id?.let { itemService.updateItemPosition(it,items.indexOf(item)+1) }
            }
        }
        //TODO : check a byte and if it is true, save the new order of the item
    }

    private val itemTouchHelperCallback = object: ItemTouchHelper.Callback() {

        override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Specify the directions of movement
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify your adapter that an item is moved from x position to y position
            binding?.recyclerView?.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            //binding?.recyclerView?.getChildItemId()
            Log.d(TAG,"Item is beeing moved + position : "+ viewHolder.adapterPosition)
            moveItemInList(target.adapterPosition,viewHolder.adapterPosition)
            itemsMoved = true
            return true
        }

        private fun moveItemInList(from : Int, to : Int) {
            Log.d(TAG,"Updating list "+ to + " "+from)
            Log.d(TAG,"Updtating the following list : ")
            val copyOfItems = items.toList()
            val localItems = items.toList()

            for(item in items) {
                Log.d(TAG,item.content.toString() + " "+ items.indexOf(item))
            }

            val fromLocation = items[from]

            Log.d(TAG, "to < from")
            items.removeAt(from)
            items.add(to,fromLocation)

            Log.d(TAG,"List edited : ")

            for(item in items) {
                Log.d(TAG,item.content.toString() + " "+ items.indexOf(item))
            }
            //TODO : mettre index à jour BDD et check boolean si besoin réécriture ou non ?
        }

        override fun isLongPressDragEnabled(): Boolean {
            // true: if you want to start dragging on long press
            // false: if you want to handle it yourself
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            // Hanlde action state changes
            if (actionState == ACTION_STATE_DRAG) {
                viewHolder?.itemView?.alpha = 0.5f
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
            // This is a good place to send update to your backend about changes
            Log.d("ItemTouchHelperCallback","Drag&Drop finished at position : "+ viewHolder.adapterPosition + "old_position : " )
            viewHolder.itemView.alpha = 1.0f
            //TODO : update items list with new order

            //viewHolder.oldPosition
            //TODO : get old position, retrieve data with firebase request and write it if necessary
            //TODO : get the item so we can update the data on FIrebase

        }   

    }

}
