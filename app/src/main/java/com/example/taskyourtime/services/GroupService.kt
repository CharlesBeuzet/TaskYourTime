package com.example.taskyourtime.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.Group
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

interface GroupService {
    fun createGroup(name: String, ownerId: String) : LiveData<Boolean>
    fun deleteGroup(groupId: String) : LiveData<Boolean>
    fun addUser(groupId: String, userId: String) : LiveData<Boolean>
    fun removeUser(groupId: String, userId: String) : LiveData<Boolean>
    fun findGroupById(idGroup: String) : LiveData<Group?>

}

class GroupServiceImpl(
    private val context: Context
) : GroupService{
    private val database : DatabaseReference = Firebase.database.reference
    private val TAG : String = "GroupService"

    override fun createGroup(name: String, ownerId: String): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val owner = Firebase.auth.currentUser
        if(owner != null && ownerId == owner.uid){
            create(name, owner.uid)
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la création du groupe.")
        }
        return success
    }

    override fun deleteGroup(groupId: String): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var groupToDelete = findGroupById(groupId)
        if(groupToDelete != null){
            delete(groupId)
            Log.w(TAG, "Succès lors de la suppression du groupe '$groupId'")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la suppression")
        }
        return success
    }

    override fun addUser(groupId:String, userId: String): LiveData<Boolean> {
        //à faire
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        success.postValue(true)

        return success
    }

    override fun removeUser(groupId:String, userId: String): LiveData<Boolean> {
        //à faire

        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        success.postValue(true)

        return success
    }

    override fun findGroupById(idGroup: String): LiveData<Group?> {
        var groupResult: MutableLiveData<Group?> = MutableLiveData<Group?>()
        database.child("groups").child(idGroup).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val userIdList: List<String?> = listOf()
            val group = Group("", "", "", userIdList as List<String>)
            group.loadFromMap(map)
            Log.d(TAG, "Groupe trouvé")
            groupResult.postValue(group)
        }.addOnFailureListener{
            Log.d(TAG, "Groupe non trouvé")
            groupResult.postValue(null)
        }
        return groupResult
    }

    private fun add(idGroup:String, userId: String){
        if(userId != null){
            database.child("groups").child(idGroup).child("userIdList").child(userId).setValue("no")
        }
    }

    private fun create(name: String, ownerId: String){
        val key = database.child("groups").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for groups")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        //on crée une liste vide d'id utilisateurs qui représenteront
        //tous les utilisateurs du groupe, à la création cette liste est vide.
        val userIdList: List<String?> = listOf()
        val group: Group = Group(key, ownerId, name, userIdList as List<String>)
        database.child("groups").child(key).setValue(group.toMAp())
    }

    private fun delete(groupId: String){
        database.child("groups").child(groupId).removeValue()
        Log.d(TAG, "Groupe dont l'id est : $groupId a été supprimé")
    }
}