package com.example.taskyourtime.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskyourtime.model.Group
import com.example.taskyourtime.model.Publication
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities

interface GroupService {
    fun createGroup(name: String, ownerId: String, userList: HashMap<String?, Any?>) : LiveData<Boolean>
    fun deleteGroup(groupId: String) : LiveData<Boolean>
    fun addUser(groupId: String, userId: String) : LiveData<Boolean>
    fun removeUser(groupId: String, userId: String) : LiveData<Boolean>
    fun findGroupById(idGroup: String) : LiveData<Group?>

    fun createPublication(title: String, groupId: String, userPublisherId: String,
                          type: String, noteId: String, calendarEventId: String,
                          toDoListId: String) : LiveData<Boolean>
    fun deletePublication(publicationId: String) : LiveData<Boolean>
    fun findPublicationById(publicationId: String) : LiveData<Publication?>
    fun findPublicationsByGroup(groupId: String) : LiveData<MutableList<Publication?>?>

}

class GroupServiceImpl(
    private val context: Context
) : GroupService{
    private val database : DatabaseReference = Firebase.database.reference
    private val TAG : String = "GroupService"

    override fun createGroup(name: String, ownerId: String, userList: HashMap<String?, Any?>): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val owner = Firebase.auth.currentUser
        if(owner != null && ownerId == owner.uid){
            create(name, owner.uid, userList)
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
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var groupToUpdate = findGroupById(groupId)
        if(groupToUpdate != null){
            add(groupId, userId)
            success.postValue(true)
            Log.w(TAG, "Succès lors de l'ajout de l'id utilisateur dans  le groupe")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de l'ajout de l'utilisateur dans le groupe")
        }
        return success
    }

    override fun removeUser(groupId:String, userId: String): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var groupToUpdate = findGroupById(groupId)
        if(groupToUpdate != null){
            remove(groupId, userId)
            success.postValue(true)
            Log.w(TAG, "Succès lors du retrait de l'id utilisateur du groupe")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors du retrait de l'utilisateur du groupe")
        }
        return success
    }

    override fun findGroupById(idGroup: String): LiveData<Group?> {
        var groupResult: MutableLiveData<Group?> = MutableLiveData<Group?>()
        database.child("groups").child(idGroup).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val userIdList: List<String?> = listOf()
            val group = Group("", "", "", userIdList as HashMap<String?, Any?>)
            group.loadFromMap(map)
            Log.d(TAG, "Groupe trouvé")
            groupResult.postValue(group)
        }.addOnFailureListener{
            Log.d(TAG, "Groupe non trouvé")
            groupResult.postValue(null)
        }
        return groupResult
    }

    private fun add(idGroup: String, userId: String){
        if(userId != null){
            database.child("groups").child(idGroup).child("userIdList").child(userId).setValue("no")
        }
    }

    private fun remove(idGroup: String, userId: String){
        if(userId != null){
            database.child("groups").child(idGroup).child("userIdList").child(userId).removeValue()
        }
    }

    private fun create(name: String, ownerId: String, userList: HashMap<String?, Any?>){
        val key = database.child("groups").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for groups")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        //on crée une liste vide d'id utilisateurs qui représenteront
        //tous les utilisateurs du groupe, à la création cette liste est vide.
        val userIdList: List<String?> = listOf()
        val group: Group = Group(key, ownerId, name, userList)
        database.child("groups").child(key).setValue(group.toMAp())
    }

    private fun delete(groupId: String){
        database.child("groups").child(groupId).removeValue()
        Log.d(TAG, "Groupe dont l'id est : $groupId a été supprimé")
    }

    override fun createPublication(
        title: String,
        groupId: String,
        userPublisherId: String,
        type: String,
        noteId: String,
        calendarEventId: String,
        toDoListId: String
    ): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        val publisher = Firebase.auth.currentUser
        if(publisher != null && userPublisherId == publisher.uid){
            addPublication(
                title,
                groupId,
                userPublisherId,
                type,
                noteId,
                calendarEventId,
                toDoListId)
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la création du groupe.")
        }
        return success
    }

    private fun addPublication(title: String,
                               groupId: String,
                               userPublisherId: String,
                               type: String,
                               noteId: String,
                               calendarEventId: String,
                               toDoListId: String){
        val key = database.child("publications").push().key
        if(key == null){
            Log.w(TAG, "Couldn't get push key for publications")
            return
        }
        Log.d(TAG, "Success in getting the key: $key")
        val publication: Publication = Publication(
            key,
            title,
            groupId,
            userPublisherId,
            type,
            noteId,
            calendarEventId,
            toDoListId)
        database.child("publications").child(key).setValue(publication.toMap())
    }

    private fun removePublication(publicationId: String){
        database.child("publications").child(publicationId).removeValue()
        Log.d(TAG, "Publication dont l'id est : $publicationId a été supprimé")
    }

    override fun deletePublication(publicationId: String): LiveData<Boolean> {
        var success : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
        var publicationToDelete = findPublicationById(publicationId)
        if(publicationToDelete != null){
            delete(publicationId)
            Log.w(TAG, "Succès lors de la suppression de la publication '$publicationId'")
        }else{
            success.postValue(false)
            Log.w(TAG, "Erreur lors de la suppression")
        }
        return success
    }

    override fun findPublicationById(publicationId: String): LiveData<Publication?> {
        var publicationResult: MutableLiveData<Publication?> = MutableLiveData<Publication?>()
        database.child("publications").child(publicationId).get().addOnSuccessListener {
            val map = it.value as Map<String?, Any?>
            val p = Publication("", "", "", "", "", "", "", "")
            p.loadFromMap(map)
            Log.d(TAG, "Publication trouvée")
            publicationResult.postValue(p)
        }.addOnFailureListener{
            Log.d(TAG, "Publication non trouvée")
            publicationResult.postValue(null)
        }
        return publicationResult
    }

    override fun findPublicationsByGroup(groupId: String): LiveData<MutableList<Publication?>?> {
        var publications: MutableLiveData<MutableList<Publication?>?> = MutableLiveData<MutableList<Publication?>?>()
        database.child("publications").orderByChild("groupId").equalTo(groupId).get().addOnSuccessListener {
            val test = it.value as Map<String?, Any?>
            val p = Publication("", "", "", "", "", "", "", "")
            p.loadFromMap(test)
            publications.value?.add(p)
        }.addOnFailureListener{
            Log.d(TAG, "publications non trouvées")
            publications.postValue(null)
        }
        return publications
    }
}