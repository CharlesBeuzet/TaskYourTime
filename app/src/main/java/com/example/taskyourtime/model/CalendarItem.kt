package com.example.taskyourtime.model

import java.util.*

class CalendarItem(
    var id: String?,
    var name: String?,
    var description: String?,
    var begin_date: Date?,
    var end_date: Date?,
    var user_id: String?
) {
    constructor(map: Map<String?, Any?>) : this(
        null,
        map["name"] as String?,
        map["description"] as String?,
        map["begin_date"] as Date?,
        map["end_date"] as Date?,
        map["user_id"] as String?
    )

    fun loadFromMap(map: Map<String?, Any?>){
        id = map["id"] as String?
        name = map["name"] as String?
        description = map["description"] as String?
        begin_date = map["begin_date"] as Date?
        end_date = map["end_date"] as Date?
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