package com.santiago.dscrum_k.Socket

import android.util.Log
import org.phoenixframework.PhxSocket

fun conexion_socket(token: String): PhxSocket {
    val params = hashMapOf("token" to token)
    val socket = PhxSocket("http://10.0.3.41:4000/socket/websocket", params)

    // Listen to events on the Socket
    socket.logger = { Log.d("TAG", it) }
    socket.onOpen { Log.d("TAG", "Socket Opened") }
    socket.onClose { Log.d("TAG", "Socket Closed") }
    socket.onError { throwable, response -> Log.d("Socket Error ${response}", "TAG", throwable) }

    socket.connect()
    return socket

    // Join channels and listen to events
    /*
    val chatroom = socket.channel("history:1")

    chatroom.on("new_story") {
        // `it` is a PhxMessage object
        println("NUEVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    }

    chatroom.join()
        .receive("ok") { println("ME HE UNIDO MUY CHIDO") }
        .receive("error") { println("ESTOY FALLANDO AYUDAME PLOX") }

    */
}