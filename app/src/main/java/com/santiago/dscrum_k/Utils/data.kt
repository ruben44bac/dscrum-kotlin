package com.santiago.dscrum_k.Utils


import android.content.SharedPreferences
import com.santiago.dscrum_k.Api.user_response

//var api_url = "https://dscrum.santiago.mx/api"
//var ws_url = "wss://dscrum.santiago.mx/socket/websocket"
var api_url = "http://10.0.3.43:4000/api"
var ws_url = "ws://10.0.3.43:4000/socket/websocket"
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

fun destroy_token(session: SharedPreferences) {
    var editor = session.edit()
    editor.putString("token", "")
    editor.apply()
}

fun destroy_user(session: SharedPreferences) {
    var editor = session.edit()

    editor.putInt("id", 0)
    editor.putString("mail", "")
    editor.putString("name", "")
    editor.putString("surname", "")
    editor.putInt("team_id", 0)
    editor.putString("username", "")
    editor.apply()
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

