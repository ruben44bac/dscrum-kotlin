package com.santiago.dscrum_k.Activities.ui.login

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.santiago.dscrum_k.Api.post

import com.santiago.dscrum_k.R
import com.santiago.dscrum_k.Utils.get_token
import com.santiago.dscrum_k.Utils.set_token
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import com.google.gson.Gson
import com.santiago.dscrum_k.Api.login_response
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        var session = getSharedPreferences("dscrum", Context.MODE_PRIVATE)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer
            println("EN EL OBSERVABLE")
            println(get_token(session))
            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {

                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                val Json = """
                {
                "username": "${username.text}",
                "password": "${password.text}"
                }
            """.trimIndent()
                post("auth", Json)!!
                    .enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println(e)
                        }
                        override fun onResponse(call: Call, response: Response) {
                            val data_response = response.body()?.string()
                            println(data_response)
                            val res =  Gson().fromJson(data_response, login_response::class.java)
                            println(res)
                            when(res.error) {
                                null -> {
                                    set_token(session, res.token!!)
                                    updateUiWithUser(username.text.toString())
                                }
                                else -> { badValues(res.error!!) }
                            }
                        }
                    })

            }

        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun updateUiWithUser(username: String) {
        thread (name = "MensajeBien", priority = 1) {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "Bienvenido $username",
                    Toast.LENGTH_LONG
                ).show()
                this.finish()
            }
        }
    }

    private fun badValues(error: String) {
        thread (name = "MensajeBien", priority = 1){
            runOnUiThread {
                Toast.makeText(applicationContext,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}
