package com.example.gasytravel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.gasytravel.ui.login.LoginActivity

class ActivitySplash : Activity() {

    private val SPLASH_SCREEN_DELAY: Long = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("my_token", "")

        Handler().postDelayed({
            Log.e("DEBUG", "token $token")
            if(token == null || token == ""){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, ScrollingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_SCREEN_DELAY)
    }
}
