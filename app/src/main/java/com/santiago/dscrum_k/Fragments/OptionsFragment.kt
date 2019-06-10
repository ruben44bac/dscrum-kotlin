package com.santiago.dscrum_k.Fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.santiago.dscrum_k.Adapters.ItemAdapter

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Utils.Item
import kotlinx.android.synthetic.main.fragment_options.*
import kotlinx.android.synthetic.main.item_recycler_options.view.*




class OptionsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onStart() {
        super.onStart()
        val items =  getLists()
        val rv_item = list_options as RecyclerView

        rv_item.layoutManager = LinearLayoutManager(this.context!!)
        rv_item.hasFixedSize()
        rv_item.adapter = ItemAdapter(items)

        rv_item.layoutManager = LinearLayoutManager(this.context!!)
        rv_item.adapter = ItemAdapter(items)

    }

    fun getLists(): ArrayList<Item> {
        var lists = ArrayList<Item>()
        lists.add(Item(1, "Perfil"))
        lists.add(Item(2, "Cerrar sesión"))
        return lists;
    }

}
