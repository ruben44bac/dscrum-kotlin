package com.santiago.dscrum_k.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.santiago.dscrum_k.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ListStoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_stories, container, false)
    }

    override fun onStart() {
        super.onStart()
        println("EN EL ONSTART ---------> 1")
    }

    override fun onResume() {
        super.onResume()
        println("EN EL ONRESUME ------------> 2")
    }

    override fun onPause() {
        super.onPause()
        println("En el ONPAUSE -----------------> 3")
    }

    override fun onStop() {
        super.onStop()
        println("En el ONSTOP ----------------------> 4")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("En el ONDESTROY -------------------------> 5")
    }
}
