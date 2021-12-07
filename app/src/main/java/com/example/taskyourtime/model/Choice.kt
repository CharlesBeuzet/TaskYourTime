package com.example.taskyourtime.model

/**
 * classe Choice qui symbolise un choix d'objet à publier dans
 * le fil de publication d'un groupe.
 */
data class Choice(
    var id: String?, //l'id prendra l'id d'une note, d'un event ou d'un item de todolist.
    var name: String?,
    var content: String?,
    var beginDate: String?,
    var endDate: String?,
    var checked: Boolean?, //il est choisi pour être publié
    var type: String?, // NOTE || CALENDAR || TODO
) {

    constructor(map: Map<String?, Any?>): this(
        map["id"] as String?,
        map["name"] as String?,
        map["content"] as String?,
        map["beginDate"] as String?,
        map["endDate"] as String?,
        map["checked"] as Boolean?,
        map["type"] as String?,
    )
}