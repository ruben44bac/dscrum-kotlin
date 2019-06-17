package com.santiago.dscrum_k.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.santiago.dscrum_k.Api.user_response
import com.santiago.dscrum_k.MainActivity

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Socket.*
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.api_url
import com.santiago.dscrum_k.Utils.get_session
import com.santiago.dscrum_k.Utils.get_token
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import kotlin.math.roundToInt


class HomeFragment : Fragment() {

    var token: String = ""
    val mapper = jacksonObjectMapper()
    private  lateinit var resp: home_data
    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private lateinit var session: SharedPreferences
    private lateinit var user: user_response

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = this.getActivity()!!.getSharedPreferences("dscrum", Context.MODE_PRIVATE)


    }

    override fun onStart() {
        super.onStart()

        view_back.animate()
            .setDuration(1000)
            .setStartDelay(200)
            .translationY(-150f)

        button_home.animate()
            .setDuration(1000)
            .setStartDelay(200)
            .translationY(90f)

        token = get_token(session)
        sockets()
        user = get_session(session)
        Picasso.get()
            .load("$api_url/team-image?id=${user.team_id}")
            .transform(CircleTransform())
            .into(image_home)
        button_home.setOnClickListener {
            (activity as MainActivity).openMindState()
        }

    }

    fun sockets(){
        socket = conexion_socket(token)
        socket.onError { throwable, response -> show_error(view = this.view!!) }
        channel = socket.channel("user:account")

        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }

        val values = mutableMapOf("" to "")
        channel.push("dashboard", values)
            .receive("ok") {
                var resp = mapper.convertValue(it.payload["response"], home_response::class.java)
                println(resp)
                activity!!.runOnUiThread {
                    if(resp.data != null && team_name != null){
                        team_name.text = resp.data!!.team_name
                        open_text.text = resp.data!!.story_open!!.roundToInt().toString()
                        close_text.text = resp.data!!.story_close!!.roundToInt().toString()
                    }

                }
            }
            .receive("error") { println("error join") }
    }

    override fun onStop() {
        super.onStop()
        socket.disconnect()
    }
    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

}


