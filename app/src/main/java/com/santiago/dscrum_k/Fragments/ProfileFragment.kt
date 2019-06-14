package com.santiago.dscrum_k.Fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.santiago.dscrum_k.Api.user_response
import com.santiago.dscrum_k.MainActivity

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Utils.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_profile.*



class ProfileFragment : Fragment() {

    var token = ""
    private lateinit var user: user_response
    private lateinit var session: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = context!!.getSharedPreferences("dscrum", Context.MODE_PRIVATE)

        token = get_token(session)

    }

    override fun onStart() {
        super.onStart()
        logout_profile.setOnClickListener {
            destroy_token(session = session)
            destroy_user(session)
            (activity as MainActivity).login()
        }
        user = get_session(session)
        Picasso.get()
            .load("$api_url/user-image?id=${user.id}")
            .transform(CircleTransform())
            .into(image_user_team)
        username_profile.text = user.username
        name_profile.text = "${user.name} ${user.surname}"
        if(user.id == 0) {
            (activity as MainActivity).consultaUsuario(true)
        }
    }

    fun get_user(userr: user_response) {
        activity!!.runOnUiThread {
            Picasso.get()
                .load("$api_url/api/user-image?id=${userr.id}")
                .transform(CircleTransform())
                .into(image_user_team)
            username_profile.text = userr.username
            name_profile.text = "${userr.name} ${userr.surname}"
        }
    }


}
