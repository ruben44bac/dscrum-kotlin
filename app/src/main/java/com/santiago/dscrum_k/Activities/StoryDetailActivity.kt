package com.santiago.dscrum_k.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.santiago.dscrum_k.Fragments.ListStoriesFragment
import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.SelectedStoryFragmentDelegate
import com.santiago.dscrum_k.Socket.*
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.get_token
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_story_detail.*
import kotlinx.android.synthetic.main.content_story_detail.*
import kotlinx.android.synthetic.main.fragment_list_stories.*
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket



class StoryDetailActivity : AppCompatActivity() {

    var story_id: Int? = null
    var token: String = ""
    var name: String = ""
    var team_list = ArrayList<story_detail_user>()

    private lateinit var adapter: grid_adapter_detail
    private lateinit var gv: GridView
    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private lateinit var data: story_detail_data
    val mapper = jacksonObjectMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        story_id = intent.getIntExtra("story_id", 0)
        name = intent.getStringExtra("name")
        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        token = get_token(session)

        setContentView(R.layout.activity_story_detail)
        setSupportActionBar(toolbar_title)
        toolbar_title.setTitle(this.name)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onStart() {
        sockets()
        gv = team_grid as GridView
        adapter = grid_adapter_detail(this.baseContext, team_list)
        println(adapter)
        gv.adapter = adapter
        super.onStart()


    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
    }

    private fun change_data() {
        runOnUiThread {
            when(data.date_start) {
                null -> {
                    text_date_end.text = "Pendiente"
                    text_date_start.text = "Pendiente"
                }
                else -> {
                    text_date_end.text = data.date_end
                    text_date_start.text = data.date_start
                }
            }
            when(data.difficulty_id){
                null -> { chart_id.setProgress(0f, true) }
                else -> {
                    val chart = data.difficulty_id!!.toFloat() * 10
                    println("El valor de la dificultad es: $chart")
                    chart_id.setProgress(chart + 1, true)
                }
            }

            adapter.notifyDataSetChanged()
        }
    }

    fun sockets(){
        socket = conexion_socket(token)
        channel = socket.channel("story_detail:${story_id}")

        channel.on("online") {
            println(it.payload)
            val res = mapper.convertValue(it.payload, story_detail_user_online::class.java)
            println(res)
            val index = team_list.mapIndexed{ i, b -> b.id }.indexOf(res.user_id!!)
            if(index > -1){
                team_list[index].online = true
                change_data()
            }
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
