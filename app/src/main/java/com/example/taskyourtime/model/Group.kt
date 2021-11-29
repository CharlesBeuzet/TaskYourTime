package com.example.taskyourtime.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Group(
    var id : String?,
    var ownerId: String?,
    var name : String?,
    var userIdList: List<String?>
) {
    constructor(map: Map<String?, Any?>): this(
        null,
        map["ownerId"] as String?,
        map["name"] as String?,
        listOf()
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        ownerId = map["ownerId"] as String?
        name = map["name"] as String?
        userIdList = map["userIdList"] as List<String?>
    }

    fun toMAp(): Map<String, Any?>?{
        val map: HashMap<String, Any?> = HashMap()
        map["ownerId"] = ownerId
        map["name"] = name
        map["userIdList"] = userIdList

        return map
    }

}