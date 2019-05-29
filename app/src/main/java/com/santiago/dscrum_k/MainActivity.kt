package com.santiago.dscrum_k

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.santiago.dscrum_k.Activities.ui.login.LoginActivity
import com.santiago.dscrum_k.Fragments.HomeFragment
import com.santiago.dscrum_k.Fragments.ListStoriesFragment
import com.santiago.dscrum_k.Fragments.LoginFragment
import com.santiago.dscrum_k.Utils.addFragmentToActivity

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), HomeFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), ListStoriesFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                addFragmentToActivity(
                    getSupportFragmentManager(), LoginFragment(), R.id.fragmets_view)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        addFragmentToActivity(
            getSupportFragmentManager(), HomeFragment(), R.id.fragmets_view)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this@MainActivity,LoginActivity::class.java);
        startActivity(intent);
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
