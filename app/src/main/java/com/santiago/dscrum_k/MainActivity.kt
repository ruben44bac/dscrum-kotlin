package com.santiago.dscrum_k

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.santiago.dscrum_k.Activities.StoryDetailActivity
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
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.collections.Map as Map1

class MainActivity : AppCompatActivity(), SelectedStoryFragmentDelegate {

    var user = user_response()
    var token: String = ""
//    var socket = PhxSocket("", hashMapOf("" to ""))
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
     when (item.itemId) {
            R.id.navigation_home -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), HomeFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                var fragmentList = ListStoriesFragment.newInstance(user.team_id)
                addFragmentToActivity(
                    getSupportFragmentManager(), fragmentList, R.id.fragmets_view)
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
        token = get_token(session)
        if(token == ""){
            login()
        } else {
            consultaUsuario()
        }
    }

    fun consultaUsuario() {
        get_token("user", token)
            .enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }
            override fun onResponse(call: Call, response: Response) {
                val data_response = response.body()?.string()
                println(data_response)
                user =  Gson().fromJson(data_response, user_response::class.java)
                when(user.error) {
                    null -> { /*socket(token)*/  }
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

    override fun onSelectedStory(valor: Int) {
        val intent = Intent(this@MainActivity, StoryDetailActivity::class.java)
        intent.putExtra("story_id", valor)
        startActivity(intent)
    }

}

interface SelectedStoryFragmentDelegate {
    fun onSelectedStory(valor: Int)
}