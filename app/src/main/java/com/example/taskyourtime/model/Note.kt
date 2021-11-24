package com.example.taskyourtime.model


import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Note(
    var id: String?,
    var name: String?,
    var content: String?,
    var user_id: String?,
) : Serializable {
    constructor(map: Map<String?, Any?>) : this(
        null,
        map["name"] as String?,
        map["content"] as String?,
        map["user_id"] as String?
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        name = map["name"] as String?
        content = map["content"] as String?
        user_id = map["user_id"] as String?
    }

    fun toMap(): Map<String, Any?>? {
        val map: HashMap<String, Any?> = HashMap()
        map["name"] = name
        map["content"] = content
        map["user_id"] = user_id

        return map
    }
}
