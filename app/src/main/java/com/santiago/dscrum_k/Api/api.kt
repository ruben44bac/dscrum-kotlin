package com.santiago.dscrum_k.Api

import okhttp3.*
import java.io.IOException
import android.R.string
import com.santiago.dscrum_k.Utils.api_url
import okhttp3.RequestBody


private val client = OkHttpClient()

fun get_token(url: String, token: String): Call {
    val request = Request.Builder()
        .addHeader("Authorization","Bearer $token")
        .url("$api_url/$url")

        .build()
    println(request.headers())
    return client.newCall(request)
}

fun post(url: String, json: String): Call {
    println(json)
    val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
    val request = Request.Builder()
        .url("$api_url/$url")
        .post(body)
        .build()
    println(body)
    return client.newCall(request)
}
