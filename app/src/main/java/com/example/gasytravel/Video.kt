package com.example.gasytravel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View

class Video : AppCompatActivity() {
    companion object {
        const val EXTRA_VIDEO_URL = "videoUrl"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)

        if (videoUrl != null) {
            val videoPlayerFragment = VideoPlayer.newInstance(videoUrl)
            supportFragmentManager.beginTransaction()
                .replace(R.id.videoContainer, videoPlayerFragment)
                .commit()
            hideSystemUI()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle back button click here
        if (item.itemId == android.R.id.home) {
            // Perform your desired action here, e.g., call finish() to navigate back
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
}