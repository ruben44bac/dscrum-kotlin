package com.santiago.dscrum_k.Activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Socket.conexion_socket
import com.santiago.dscrum_k.Socket.mind_data
import com.santiago.dscrum_k.Socket.mind_response
import com.santiago.dscrum_k.Socket.show_error
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.api_url
import com.santiago.dscrum_k.Utils.get_token
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mind.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import android.widget.ImageButton



class MindActivity : AppCompatActivity() {

    var token: String = ""
    val mapper = jacksonObjectMapper()
    var list_mind = ArrayList<mind_data>()

    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind)
        session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        token = get_token(session)
        sockets()

    }

    fun sockets(){
        socket = conexion_socket(token)
        socket.onError { throwable, response -> show_error(view = findViewById(android.R.id.content)) }
        channel = socket.channel("mind_state:state")
        channel.onError {
            println("ERROR EN EL CHANNEL DDDDDDD:")
        }



        channel.onError {
            socket.startHeartbeatTimer()
        }

        channel.onClose {
        }

        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }
        val values = mutableMapOf("" to "")
        channel.push("list",values)
            .receive("ok", callback = {
                println(it)
                var resp = mapper.convertValue(it.payload["response"], mind_response::class.java)
                list_mind = resp.data!!
                set_buttons()
            })
            .receive("error", {
                println(it)
            })
    }

    fun set_buttons(){

        if(list_mind.count() > 0){
            runOnUiThread {
                Picasso.get()
                    .load("$api_url/mind-state-image?id=${list_mind[0].id}")
                    .transform(CircleTransform())
                    .into(mind_image_2)

                Picasso.get()
                    .load("$api_url/mind-state-image?id=${list_mind[1].id}")
                    .transform(CircleTransform())
                    .into(mind_image_1)

                Picasso.get()
                    .load("$api_url/mind-state-image?id=${list_mind[2].id}")
                    .transform(CircleTransform())
                    .into(mind_image_3)
            }
        }
    }
}
