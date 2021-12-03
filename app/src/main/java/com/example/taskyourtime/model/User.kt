package com.example.taskyourtime.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class User(
    var firstName : String?,
    var lastName : String?,
    var email : String?,
    var profilePicture : String?,
) : Serializable{
    constructor(map: Map<String?, Any?>) : this(
        map["firstName"] as String?,
        map["lastName"] as String?,
        map["email"] as String?,
        map["profilePicture"] as String?
    )

    fun loadFromMap(map: Map<String?, Any?>) {
        firstName = map["firstName"] as String?
        lastName = map["lastName"] as String?
        email = map["email"] as String?
        profilePicture = map["profilePicture"] as String?
    }

    fun toMap(): Map<String, Any?>? {
        val map: HashMap<String, Any?> = HashMap()
        map["firstName"] = firstName
        map["lastName"] = lastName
        map["email"] = email
        map["profilePicture"] = profilePicture

        return map
    }
}
