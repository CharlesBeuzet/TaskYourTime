package com.example.taskyourtime.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class GroupValidation(
    var id : String?,
    var userId: String?,
    var groupId : String?,
    var validated: String?
): Serializable {
    constructor(map: Map<String?, Any?>): this(
        null,
        map["userId"] as String?,
        map["groupId"] as String?,
        map["validated"] as String?
    )

    fun toMAp(): Map<String, Any?>?{
        val map: HashMap<String, Any?> = HashMap()
        map["userId"] = userId
        map["GroupId"] = groupId
        map["validated"] = validated
        return map
    }

}