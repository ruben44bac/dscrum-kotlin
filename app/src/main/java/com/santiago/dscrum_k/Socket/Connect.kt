package com.santiago.dscrum_k.Socket

import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.santiago.dscrum_k.Utils.ws_url
import org.phoenixframework.PhxSocket

fun conexion_socket(token: String): PhxSocket {
    val params = hashMapOf("token" to token)
    val socket = PhxSocket(ws_url, params)

    // Listen to events on the Socket
    socket.logger = { Log.d("TAG", it) }
    socket.onOpen { Log.d("TAG", "Socket Opened") }
    socket.onClose { Log.d("TAG", "Socket Closed") }
    socket.onError { throwable, response -> Log.d("Socket Error ${response}", "TAG", throwable) }

    socket.connect()



    return socket

}


fun show_error(view: View){
    val snackbar = Snackbar.make(view, "Te has quedado sin conexión :(",
        Snackbar.LENGTH_LONG).setAction("OK", null)
    snackbar.setActionTextColor(Color.BLUE)
    snackbar.setDuration(10000)
    val snackbarView = snackbar.view
    snackbarView.setBackgroundColor(Color.RED)
    snackbar.show()
}