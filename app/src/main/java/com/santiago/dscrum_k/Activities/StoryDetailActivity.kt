package com.santiago.dscrum_k.Activities

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Socket.conexion_socket
import com.santiago.dscrum_k.Socket.story_detail_response
import com.santiago.dscrum_k.Utils.get_token
import kotlinx.android.synthetic.main.activity_story_detail.*
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket







class StoryDetailActivity : AppCompatActivity() {

    var story_id: Int? = null
    var token: String = ""
    var nombre: String = "Tarea"

    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    val mapper = jacksonObjectMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        story_id = intent.getIntExtra("story_id", 0)
        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        token = get_token(session)

        setContentView(R.layout.activity_story_detail)
        setSupportActionBar(toolbar_title)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        sockets()
    }

    private fun change_title() {
        this!!.runOnUiThread {
            println(this)
            println("jajajajajajajajajjaja")
            println(this.nombre)
            toolbar_title.setTitle(this.nombre)
        }
    }

    fun sockets(){
        socket = conexion_socket(token)
        channel = socket.channel("story_detail:${story_id}")
        channel.on("online") {
            //val res = mapper.convertValue(it.payload, stories_array::class.java)
        }
        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }

        val values = mutableMapOf("" to "")

        channel.push("index",values)
            .receive("ok", callback = {
                val res = mapper.convertValue(it.payload.get("response"), story_detail_response::class.java)
                nombre = res.data!!.name!!
                change_title()
            })
            .receive("error", {
                println(it)
            })

    }
}
