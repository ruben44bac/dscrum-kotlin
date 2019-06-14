package com.santiago.dscrum_k.Fragments


import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.fasterxml.jackson.module.kotlin.*

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Socket.conexion_socket
import com.santiago.dscrum_k.Socket.show_error
import com.santiago.dscrum_k.Socket.stories_array
import com.santiago.dscrum_k.Socket.stories_response
import com.santiago.dscrum_k.Utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_list_stories.*
import org.phoenixframework.PhxChannel
import org.phoenixframework.PhxSocket
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat


class ListStoriesFragment : Fragment() {

    var team_id: Int = 0
    var token: String = ""
    val mapper = jacksonObjectMapper()
    var story_list = ArrayList<stories_array>()
    var delegate: SelectedStoryFragmentDelegate? = null
    private lateinit var adapter: grid_adapter
    private lateinit var gv: GridView
    private lateinit var socket: PhxSocket
    private lateinit var channel: PhxChannel
    private var page = pag(0,10,10)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var session = this.getActivity()!!.getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        team_id = arguments?.getInt("team_id")!!
        token = get_token(session)
        return inflater.inflate(R.layout.fragment_list_stories, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is SelectedStoryFragmentDelegate) {
            delegate = context
        }
    }

    override fun onStart() {
        super.onStart()
        when(story_list.count()){
            0 -> {
                incializa()
            }
            else -> {
                sockets(false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        socket.disconnect()
    }

    fun incializa(){
        sockets(true)
        gv = stories_grid as GridView
        adapter = grid_adapter(this.context!!, story_list, delegate!!)
        gv.adapter = adapter
        gv.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - visibleItemCount <= firstVisibleItem && adapter.count + story_list.count() < (page.total)) {
                    page.index ++
                    push_stories()
                }
            }
            override fun onScrollStateChanged(view: AbsListView?, state: Int) {
            }
        })

    }

    companion object{
        fun newInstance(team_id: Int): ListStoriesFragment {
            val args = Bundle()
            args.putInt("team_id", team_id)
            val fragment = ListStoriesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun sockets(push: Boolean){
        socket = conexion_socket(token)
        socket.onError { throwable, response -> show_error(view = this.view!!) }
        channel = socket.channel("history:${team_id}")
        channel.on("new_story") {
            val res = mapper.convertValue(it.payload, stories_array::class.java)
            this.story_list.add(0, res)
            this.page.total ++
            activity!!.runOnUiThread { adapter.notifyDataSetChanged() }
        }
        channel.join()
            .receive("ok") { println("Join channel") }
            .receive("error") { println("error join") }
        if(push)
            push_stories()
    }

    fun push_stories(){
        val values = mutableMapOf("index" to page.index, "size" to page.size)
        channel.push("list", values)
            .receive("ok", callback = { (stories_response::class.java)
                val res = mapper.convertValue(it.payload, stories_response::class.java)
                this.story_list.addAll(res.response!!.data!!.attributes!!.stories!!)
                this.page.total = res.response!!.data!!.attributes!!.total!!
                activity!!.runOnUiThread { adapter.notifyDataSetChanged() }
            })
            .receive("error", {
                println(it)
            })
    }

    private class grid_adapter(private var c: Context, private var lista: ArrayList<stories_array>, private var delegate: SelectedStoryFragmentDelegate): BaseAdapter() {
        override fun getCount(): Int {
            return lista.count()
        }

        override fun getItem(position: Int): Any {
            return lista[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if(view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.item_grid_stories, parent, false)
            }
            val img = view!!.findViewById<ImageView>(R.id.image_difficulty)
            if(lista[position].difficulty_id == null){

            } else {
                Picasso.get()
                    .load("$api_url/difficulty-image?id=${lista[position].difficulty_id}")
                    .into(img)
            }

            val texto = view!!.findViewById<TextView>(R.id.name)
            when(lista[position].date_end){
                null -> {
                    val date = view!!.findViewById<TextView>(R.id.date_story)
                    date.isVisible = false
                }
                else -> {
                    val date = view!!.findViewById<TextView>(R.id.date_story)
                    date.isVisible = true
                    val localDateTime = LocalDateTime.parse(lista[position].date_end)
                    val formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm")
                    val output = formatter.format(localDateTime)
                    date.text = output
                }
            }


            texto.text = lista[position].name
            view.setOnClickListener(){
               delegate.onSelectedStory(lista[position].id!!, lista[position].name!!)
            }
            return view!!
        }

    }


}
