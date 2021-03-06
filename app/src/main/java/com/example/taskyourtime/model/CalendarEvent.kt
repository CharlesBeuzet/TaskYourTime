package com.example.taskyourtime.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class CalendarEvent(
    var id: String?,
    var name: String?,
    var description: String?,
    var begin_date: String?,
    var end_date: String?,
    var user_id: String?
): Serializable {
    constructor(map: Map<String?, Any?>) : this(
        null,
        map["name"] as String?,
        map["description"] as String?,
        map["begin_date"] as String?,
        map["end_date"] as String?,
        map["user_id"] as String?
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        name = map["name"] as String?
        description = map["description"] as String?
        begin_date = map["begin_date"] as String?
        end_date = map["end_date"] as String?
        user_id = map["user_id"] as String?
    }

    fun toMap(): Map<String, Any?>? {
        val map: HashMap<String, Any?> = HashMap()
        map["name"] = name
        map["description"] = description
        map["begin_date"] = begin_date
        map["end_date"] = end_date
        map["user_id"] = user_id

        return map
    }

}