package com.example.taskyourtime.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Publication(
    var id: String?,
    var title: String?,
    var groupId: String?,
    var userPublisherId: String?,
    var type: String?,
    var noteId: String?,
    var calendarEventId: String?,
    var toDoListId: String?,
    var datePublication: String?,
) {
    constructor(map: Map<String?, Any?>): this(
        null,
        map["title"] as String?,
        map["groupId"] as String?,
        map["userPublisherId"] as String?,
        map["type"] as String?,
        map["noteId"] as String?,
        map["calendarEventId"] as String?,
        map["toDoListId"] as String?,
        map["datePublication"] as String?,
    )

    fun loadFromMap(map: Map<String?, Any?>){
        map["id"] as String?
        map["title"] as String?
        map["groupId"] as String?
        map["userPublisherId"] as String?
        map["type"] as String?
        map["noteId"] as String?
        map["calendarEventId"] as String?
        map["toDoListId"] as String?
        map["datePublication"] as String?
    }

    fun toMap(): Map<String, Any?>?{
        val map: HashMap<String, Any?> = HashMap()
        map["title"] = title
        map["groupId"] = groupId
        map["userPublisherId"] = userPublisherId
        map["type"] = type
        map["noteId"] = noteId
        map["calendarEventId"] = calendarEventId
        map["toDoListId"] = toDoListId
        map["datePublication"] = datePublication

        return map
    }
}