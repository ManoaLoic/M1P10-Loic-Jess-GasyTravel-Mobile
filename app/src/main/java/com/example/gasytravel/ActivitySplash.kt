package com.example.gasytravel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler

class ActivitySplash : Activity() {

    private val SPLASH_SCREEN_DELAY: Long = 3000 // Durée du Splash Screen en millisecondes (3 secondes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Définir un délai pour passer à l'activité suivante après la durée spécifiée
        Handler().postDelayed({
            val intent = Intent(this, ScrollingActivity::class.java) // Remplacez LoginActivity par l'activité que vous souhaitez afficher après le Splash Screen
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_DELAY)
    }
}
