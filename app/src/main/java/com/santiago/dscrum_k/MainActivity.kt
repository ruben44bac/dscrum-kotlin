package com.santiago.dscrum_k

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.santiago.dscrum_k.Activities.ui.login.LoginActivity
import com.santiago.dscrum_k.Api.*
import com.santiago.dscrum_k.Fragments.HomeFragment
import com.santiago.dscrum_k.Fragments.ListStoriesFragment
import com.santiago.dscrum_k.Fragments.OptionsFragment
import com.santiago.dscrum_k.Socket.conexion_socket
import com.santiago.dscrum_k.Utils.addFragmentToActivity
import com.santiago.dscrum_k.Utils.get_token
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.collections.Map as Map1

class MainActivity : AppCompatActivity() {

    var user = user_response()
    var socket = PhxSocket("", hashMapOf("" to ""))
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), HomeFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), ListStoriesFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), OptionsFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        addFragmentToActivity(
            getSupportFragmentManager(), HomeFragment(), R.id.fragmets_view)
    }

    override fun onStart() {
        super.onStart()
        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        if(get_token(session) == ""){
            login()
        } else {
            consultaUsuario(get_token(session))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun consultaUsuario(token: String) {
        get_token("user", token)
            .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }
            override fun onResponse(call: Call, response: Response) {
                val data_response = response.body()?.string()
                println(data_response)
                user =  Gson().fromJson(data_response, user_response::class.java)
                when(user.error) {
                    null -> { socket(token) }
                    else -> { badValues() }
                }
            }
        })
    }


    private fun badValues() {
        thread (name = "SesionInvalida", priority = 1){
            runOnUiThread {
                Toast.makeText(applicationContext,
                    "Tu sesión ha caducado",
                    Toast.LENGTH_LONG
                ).show()
                login()
            }
        }

    }
    private  fun login() {
        val intent = Intent(this@MainActivity,LoginActivity::class.java);
        startActivity(intent);
    }

    private fun socket(token: String){
        socket = conexion_socket(token)
        val chatroom = socket.channel("history:${user.team_id}")

        chatroom.on("new_story") {
            // `it` is a PhxMessage object
        }

        chatroom.join()
            .receive("ok") { println("ME HE UNIDO MUY CHIDO") }
            .receive("error") { println("ESTOY FALLANDO AYUDAME PLOX") }

        val values = mutableMapOf("index" to 0, "size" to 10)

        chatroom.push("list", values)
            .receive("ok", callback = {
                println(it)
            })
            .receive("error", {
                println(it)
            })
    }
}
