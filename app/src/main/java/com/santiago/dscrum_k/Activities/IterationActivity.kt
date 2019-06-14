package com.santiago.dscrum_k.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.santiago.dscrum_k.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ramotion.fluidslider.FluidSlider
import com.santiago.dscrum_k.Socket.*
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.api_url
import com.santiago.dscrum_k.Utils.get_token
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_iteration.*
import org.aviran.cookiebar2.CookieBar
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket



class IterationActivity : AppCompatActivity() {
    var story_id: Int? = null
    var token: String = ""
    var pos_act = 0
    var difficulty_list = ArrayList<difficulty_list_data>()
    val mapper = jacksonObjectMapper()

    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private lateinit var text_diff: TextView
    private lateinit var img: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iteration)
        story_id = intent.getIntExtra("story_id", 0)
        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        token = get_token(session)
        text_diff = text_difficulty
        img = image_iteration as ImageView

    }

    @SuppressLint("ResourceType")
    override fun onStart() {
        super.onStart()
        difficulty_list.clear()
        sockets()
        button_iteration.setOnClickListener {
            send_qualify()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    fun send_qualify(){
        val values = mutableMapOf("difficulty_id" to difficulty_list[pos_act].id!!,"description" to edit_iteration.text.toString())
        println(values)
        channel.push("qualify", values)
            .receive("ok", callback = {
                val res = mapper.convertValue(it.payload.get("response"), difficulty_qualify_response::class.java)
                runOnUiThread {

                    when(res.data!!.error){
                        null -> {
                            CookieBar.build(this@IterationActivity)
                                .setTitle("Felicidades!")
                                .setMessage(res.data!!.message!!)
                                .setBackgroundColor(R.color.colorSuccessMessage)
                                .setDuration(6000)
                                .show()
                        }
                        else -> {
                            CookieBar.build(this@IterationActivity)
                                .setTitle("Hey!")
                                .setMessage(res.data!!.error)
                                .setBackgroundColor(R.color.colorErrorMessage)
                                .setDuration(6000)
                                .show()
                        }
                    }

                }
            })
            .receive("error", {
                println(it)
            })
    }

    fun conf_fluid() {
        val total = difficulty_list.count() - 1
        val max = difficulty_list.get(total).id!!
        val min = difficulty_list.get(0).id!!
        val slider = findViewById<FluidSlider>(R.id.fluidSlider)
        slider.positionListener = {
                pos -> slider.bubbleText = "${min + (total * pos).toInt()}"
                pos_act = min + (total * pos).toInt() - 1
                text_diff.text = difficulty_list[pos_act].name
                images()
        }
        slider.position = 0.5f
        pos_act = 4
        images()
        slider.startText = "$min"
        slider.endText = "$max"
        text_diff.text = difficulty_list[pos_act].name
    }

    private fun images() {
        runOnUiThread {
            Picasso.get()
                .load("$api_url/difficulty-image?id=${difficulty_list[pos_act].id}")
                .transform(CircleTransform())
                .into(img)
        }
    }

    fun sockets(){
        socket = conexion_socket(token)
        socket.onError { throwable, response -> show_error(view = findViewById(android.R.id.content)) }
        channel = socket.channel("story_detail:${story_id}")

        channel.on("close"){
            println(it.payload.get("difficulty"))
            runOnUiThread {
                val intent = Intent()
                intent.putExtra("difficulty", it.payload.get("difficulty") as Double)
                setResult(Activity.RESULT_OK, intent)
                this.finish()
            }
        }

        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }

        val values = mutableMapOf("" to "")

        channel.push("difficulty_list",values)
            .receive("ok", callback = {
                println(it)
                val res = mapper.convertValue(it.payload.get("response"), difficulty_list_response::class.java)
                println(res)
                this.difficulty_list.addAll(res.data!!)
                this.conf_fluid()
            })
            .receive("error", {
                println(it)
            })

    }

}
