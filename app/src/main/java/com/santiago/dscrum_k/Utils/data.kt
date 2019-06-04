package com.santiago.dscrum_k.Utils

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.santiago.dscrum_k.Api.user_response


fun crea_sesion(user: user_response, activity: FragmentActivity) {
    val sharedPref = activity.getSharedPreferences(
        "com.santiago.dscrum_k", Context.MODE_PRIVATE)

    with(sharedPref!!.edit()) {
        putInt("id", user.id)
        putString("mail", user.mail)
        putString("name", user.name)
        putString("surname", user.surname)
        putInt("team_id", user.team_id)
        putString("username", user.username)
        commit()
    }
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
