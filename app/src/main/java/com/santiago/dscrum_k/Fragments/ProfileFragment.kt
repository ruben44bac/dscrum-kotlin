package com.santiago.dscrum_k.Fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.santiago.dscrum_k.Api.user_response

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Utils.CircleTransform
import com.santiago.dscrum_k.Utils.get_session
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProfileFragment : Fragment() {

    private lateinit var user: user_response

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var session = context!!.getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        user = get_session(session)
        println(user)

    }

    override fun onStart() {
        super.onStart()
        logout_profile.setOnClickListener {

        }
        Picasso.get()
            .load("http://10.0.3.41:4000/api/user-image?id=${user.id}")
            .transform(CircleTransform())
            .into(image_user_team)
        username_profile.text = user.username
        name_profile.text = "${user.name} ${user.surname}"
    }


}
