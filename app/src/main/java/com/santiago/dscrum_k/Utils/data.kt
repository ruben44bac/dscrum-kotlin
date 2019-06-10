package com.santiago.dscrum_k.Utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.santiago.dscrum_k.Api.user_response


fun set_sesion(user: user_response, session: SharedPreferences) {
    var editor = session.edit()

    editor.putInt("id", user.id)
    editor.putString("mail", user.mail)
    editor.putString("name", user.name)
    editor.putString("surname", user.surname)
    editor.putInt("team_id", user.team_id)
    editor.putString("username", user.username)
    editor.apply()
}

fun get_session(session: SharedPreferences): user_response {
    val user = user_response(
        id = session.getInt("id", 0),
        mail = session.getString("mail", ""),
        name = session.getString("name", ""),
        surname = session.getString("surname", ""),
        team_id = session.getInt("team_id",0),
        username = session.getString("username", "")
    )
    return user
}

fun set_token(session: SharedPreferences, token: String) {
    var editor = session.edit()
    editor.putString("token", token)
    editor.apply()
}
fun get_token(session: SharedPreferences) : String {
    return session.getString("token", "")
}

data class pag(var index: Int, var size: Int, var total: Int)
data class Item(val id: Long, val title: String)

