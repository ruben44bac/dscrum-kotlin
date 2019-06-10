package com.santiago.dscrum_k.Activities

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kinda.alert.KAlertDialog
import com.santiago.dscrum_k.Fragments.ListStoriesFragment
import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Socket.*
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.get_token
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_story_detail.*
import kotlinx.android.synthetic.main.content_story_detail.*
import kotlinx.android.synthetic.main.fragment_list_stories.*
import org.aviran.cookiebar2.CookieBar
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class StoryDetailActivity : AppCompatActivity() {

    var story_id: Int? = null
    var token: String = ""
    var name: String = ""
    var team_list = ArrayList<story_detail_user>()
    var show_result = false
    val mapper = jacksonObjectMapper()

    private lateinit var adapter: grid_adapter_detail
    private lateinit var gv: GridView
    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private lateinit var data: story_detail_data
    private lateinit var dialog: KAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        story_id = intent.getIntExtra("story_id", 0)
        name = intent.getStringExtra("name")
        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        token = get_token(session)
        setContentView(R.layout.activity_story_detail)
        setSupportActionBar(toolbar_title)
        toolbar_title.setTitle(this.name)
        dialog = KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
        fab.setOnClickListener { view ->
            val intent = Intent(this@StoryDetailActivity,IterationActivity::class.java);
            intent.putExtra("story_id", story_id!!)
            startActivityForResult(intent, 1);
        }
    }

    override fun onStart() {
        super.onStart()

        team_list.clear()
        socket = conexion_socket(token)
        sockets()
        gv = team_grid as GridView
        adapter = grid_adapter_detail(this.baseContext, team_list)
        println(adapter)
        gv.adapter = adapter


    }

    override fun onDestroy() {
        super.onDestroy()
        val values = mutableMapOf("" to "")
        channel.push("leave",values)
        socket.disconnect()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun change_data() {
        runOnUiThread {
            when(data.status){
                true -> {
                    fab.isVisible = false
                    fab.hide()
                    }
            }
            when(data.date_start) {
                null -> {
                    text_date_end.text = "Pendiente"
                    text_date_start.text = "Pendiente"
                }
                else -> {
                    val localDateTime = LocalDateTime.parse(data.date_start)
                    val localDateTime_1 = LocalDateTime.parse(data.date_end)
                    val formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm")
                    val output = formatter.format(localDateTime)
                    val output_1 = formatter.format(localDateTime_1)
                    text_date_end.text = output_1
                    text_date_start.text = output
                }
            }
            when(data.difficulty_id){
                null -> { chart_id.setProgress(0f, true) }
                else -> {
                    val chart = data.difficulty_id!!.toFloat() * 10
                    chart_id.setProgress(chart + 1, true)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
    fun reconnect(){
        socket.disconnect()
        reconnect_1()
    }

    fun reconnect_1(){
        println("RECONECTANDO ----> ${  socket.isConnected }")
            socket.connect()
            sockets()
    }

    fun sockets(){

        socket.onError { throwable, response -> //reconnect()
            }
        channel = socket.channel("story_detail:${story_id}")

        channel.onError {
            println("ERROR EN EL CHANNEL DDDDDDD:")
        }


        channel.on("online") {
            val res = mapper.convertValue(it.payload, story_detail_user_online::class.java)
            val index = team_list.mapIndexed{ i, b -> b.id }.indexOf(res.user_id!!)
            if(index > -1 && team_list[index].online == false){
                runOnUiThread {

                    CookieBar.build(this@StoryDetailActivity)
                        .setTitle("${res.username} ha ingresado")
                        .setMessage("El usuario ha ingresado a la historia")
                        .setBackgroundColor(R.color.colorAccent)
                        .setDuration(6000)
                        .show()
                }
                team_list[index].online = true
                change_data()
            }
        }
        channel.on("left") {
            val res = mapper.convertValue(it.payload, story_detail_user_online::class.java)
            val index = team_list.mapIndexed{ i, b -> b.id }.indexOf(res.user_id!!)
            if(index > -1){
                runOnUiThread {
                    CookieBar.build(this@StoryDetailActivity)
                        .setTitle("${res.username} ha salido")
                        .setMessage("El usuario ha salido de la historia")
                        .setMessageColor(R.color.colorPrimary
                        )
                        .setTitleColor(R.color.colorPrimaryDark)
                        .setBackgroundColor(R.color.colorLeaveMessage)
                        .setDuration(6000)
                        .show()
                }
                team_list[index].online = false
                change_data()
            }
        }

        channel.on("close"){
            show_calification(it.payload.get("difficulty") as Double)
        }

        channel.onError {
            socket.startHeartbeatTimer()
        }

        channel.onClose {
            println("CLOSEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE")
        }



        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }
        val values = mutableMapOf("" to "")
        channel.push("index",values)
            .receive("ok", callback = {
                println(it)
                val res = mapper.convertValue(it.payload.get("response"), story_detail_response::class.java)
                this.data = res.data!!
                this.team_list.addAll(this.data!!.users!!)
                change_data()
            })
            .receive("error", {
                println(it)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (data != null) {
                show_calification(data.getDoubleExtra("difficulty", 0.0))
            }
        }

    }

    fun show_calification(value: Double){
        runOnUiThread {
            dialog
                .setTitleText("Promedio: $value")
                .setContentText("Todos han calificado")
                .setConfirmClickListener {
                    println("ALGO ***************************************")
                    dialog.dismissWithAnimation()
                    this.dialog = KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
                }
                .show()
        }
    }

    class grid_adapter_detail(private var c: Context, private var lista: ArrayList<story_detail_user>): BaseAdapter() {
        override fun getCount(): Int {
            return lista.count()
        }

        override fun getItem(position: Int): Any {
            return lista[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if(view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.item_grid_team, parent, false)
            }
            val elto = this.getItem(position) as story_detail_user
            val img = view!!.findViewById<ImageView>(R.id.image_user_team)
            Picasso.get()
                .load("http://10.0.3.41:4000/api/user-image?id=${lista[position].id}")
                .transform(CircleTransform())
                .into(img)
            val name = view!!.findViewById<TextView>(R.id.name_team)
            val username = view!!.findViewById<TextView>(R.id.username_team)
            val qualify = view!!.findViewById<TextView>(R.id.qualify_team)
            var view_online = view!!.findViewById<View>(R.id.view_online_team)
            username.text = lista[position].username
            when(lista[position].online){
                true -> view_online.setBackgroundResource(R.drawable.border_image)
                else -> view_online.setBackgroundResource(R.drawable.border_image_offline)
            }
            when(lista[position].difficulty_name){
                null -> qualify.height = 0
                else -> qualify.text = lista[position].difficulty_name
            }
            when(lista[position].surname){
                null -> name.text = lista[position].name
                else -> name.text = "${lista[position].name} ${lista[position].surname}"
            }

            view.setOnClickListener(){
                println("TAP TAP TAP para ${lista[position].name}")
            }
            return view!!
        }

    }
}
