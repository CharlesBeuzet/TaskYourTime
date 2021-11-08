package com.example.taskyourtime.model


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ToDoItem(
    var id: String?,
    var content: String?,
    var user_id: String?,
) {
    constructor(map: Map<String?, Any?>) : this(
        null,
        map["content"] as String?,
        map["user_id"] as String?
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        content = map["content"] as String?
        user_id = map["user_id"] as String?
    }

    fun toMap(): Map<String, Any?>? {
        val map: HashMap<String, Any?> = HashMap()
        map["content"] = content
        map["user_id"] = user_id

        return map
    }
}
