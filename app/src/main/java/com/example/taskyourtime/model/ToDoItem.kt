package com.example.taskyourtime.model


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ToDoItem(
    var id: String?,
    var content: String?,
    var user_id: String?,
    var done: Boolean?,
    var position: Long?,
) {
    constructor(map: Map<String?, Any?>) : this(
        null,
        map["content"] as String?,
        map["user_id"] as String?,
        map["done"] as Boolean?,
        map["position"] as Long?,
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        content = map["content"] as String?
        user_id = map["user_id"] as String?
        done = map["done"] as Boolean?
        position = map["position"] as Long?
    }

    fun toMap(): Map<String, Any?>? {
        val map: HashMap<String, Any?> = HashMap()
        map["content"] = content
        map["user_id"] = user_id
        map["done"] = done
        map["position"] = position

        return map
    }
}
