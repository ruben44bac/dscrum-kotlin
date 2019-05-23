package com.santiago.dscrum

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Typeface
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var perro = "lalalala"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val typeface = Typeface.createFromAsset(assets, "Oswald-Regular.ttf")
        textView2.text = "Login"
        textView2.typeface = typeface

        username_et.afterTextChanged {
            val content = username_et.text.toString()
            username_et.error =
                if (content.length <= 1 || content.length > 10) "Invalid username"
                else if (content.contains(" ")) "Username not contain white spaces"
                else null
        }
        password_et.afterTextChanged {
            val content = password_et.text.toString()
            password_et.error =
                    if(content.length > 20) "Invalid password"
                    else null
        }
    }

    override fun onStart() {
        super.onStart()
        println("ESTOY EN EL ONSTART")
    }

    override fun onResume() {
        super.onResume()
        println("ESTOY EN EL ONRESUME")
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        println("EN EL ONSAVEINSTANCESTATE")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        println("EN EL ONSAVEINSTANCESTATE")
    }

    override fun onPause() {
        super.onPause()
        println("ESTOY EN ONPAUSE")
    }

    override fun onStop() {
        super.onStop()
        println("EN EL ONSTOP--")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("ONDESTROY")
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        println("en el OnConfigurationChanged")
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
        println("en el OnCreateView")
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }


}


