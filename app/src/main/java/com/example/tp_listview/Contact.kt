package com.example.tp_listview

import java.io.Serializable

data class Contact(
    var id: Long = System.currentTimeMillis(),
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoUri: String? = null,
    var photoDrawableRes: Int? = null
) : Serializable {

    val nomComplet: String
        get() = "$nom $prenom".trim()
}
