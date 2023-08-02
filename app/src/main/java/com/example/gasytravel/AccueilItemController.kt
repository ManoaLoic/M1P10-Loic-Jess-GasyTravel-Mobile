package com.example.gasytravel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gasytravel.adapter.ScrollingActivityAdapter
import com.example.gasytravel.databinding.ActivityScrollingBinding
import com.example.gasytravel.model.TvShow
import android.os.Handler

class AccueilItemController : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add the ExampleFragment to the activity's view hierarchy
//        if (savedInstanceState == null) {
//            val fragmentTransaction = supportFragmentManager.beginTransaction()
//            val exampleFragment = AccueilItem()
//            fragmentTransaction.add(R.id.fragmentContainer, exampleFragment)
//            fragmentTransaction.commit()
//        }
    }
}