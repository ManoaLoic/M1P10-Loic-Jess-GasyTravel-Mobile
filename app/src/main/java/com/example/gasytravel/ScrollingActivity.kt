package com.example.gasytravel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.gasytravel.model.GetPostsBodyModel
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.model.UserModel
import com.example.gasytravel.service.ApiClient
import com.example.gasytravel.ui.login.LoginActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private val TAG = ScrollingActivity::class.java.simpleName
    private lateinit var mainActivityAdapter: ScrollingActivityAdapter
    private var currentPage = 1
    private var totalAvailablePages = 5
    private lateinit var posts: ArrayList<Post>
    private var isLoadMore : Boolean = false
    private var apiClient = ApiClient(this)
    private var q : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("DEBUG", "RECEVOIR Notification ${
            MyFirebaseMessagingService.PreferenceHelper.getRecevoirNotification(
                this
            )
        }")
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, PostForm::class.java)
            startActivity(intent)
        }

        binding.search.setOnClickListener { view ->
            q = binding.q.text.toString()
            currentPage = 1
            loadPageList(false)
        }

        initViews()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
//            GoogleApiAvailability.makeGooglePlayServicesAvailable()
            Log.e("DEBUG", "Granted !!!!!!!!!")
        } else {
            Log.e("DEBUG", "No !!!!!!!!!")
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initViews() {
        binding.recyclerview.setHasFixedSize(true)
        binding.recyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        posts = ArrayList()
        mainActivityAdapter = ScrollingActivityAdapter(supportFragmentManager)
        binding.recyclerview.adapter = mainActivityAdapter
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Calculate the distance between the current scroll position and the end of the RecyclerView
                val totalItemCount = recyclerView.layoutManager?.itemCount ?: 0
                val lastVisibleItemPosition = (recyclerView.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition() ?: 0
                val distanceToEnd = totalItemCount - lastVisibleItemPosition - 1

//                Log.e("DEBUG", "Onscroll event fired ${distanceToEnd}")

                // Check if the distance to the end is smaller than the specified offset
                if (distanceToEnd <= 2) {
                    if (currentPage <= totalAvailablePages && !isLoadMore) {
                        isLoadMore = true
                        Log.e("DEBUG", "Called on  ${distanceToEnd} page = ${currentPage}")
                        currentPage += 1
                        loadPageList(true)
                    }
                }
            }
        })
        loadPageList(true)
    }

    private fun loadPageList(add : Boolean) {
        toogleLoading()
        apiClient.callGetPosts(GetPostsBodyModel(currentPage, q), object : Callback<GetPostsModel> {
            override fun onResponse(call: Call<GetPostsModel>, response: Response<GetPostsModel>) {
                if (response.isSuccessful) {
                    val getPostModel = response.body()
                    if (getPostModel != null) {
                        val oldCount = posts.size
                        totalAvailablePages = getPostModel.maxPage
                        if(!add) posts = ArrayList()
                        posts.addAll(getPostModel.docs)
                        mainActivityAdapter.updateList(posts, oldCount, posts.size, add)
                        Log.e(
                            TAG,
                            "oldCount $oldCount totalAvailablePages $totalAvailablePages tvShowList ${posts.size}"
                        )
                    }
                }
                toogleLoading()
            }

            override fun onFailure(call: Call<GetPostsModel>, t: Throwable) {
                t.printStackTrace()
                Log.e(TAG, "exception", t)
                toogleLoading()
            }
        })
    }

    private fun toogleLoading() {
        Log.e("Debug", "Current page for Loading ${currentPage}")
        if (currentPage == 1) {
            if (binding.defaultProgress.isShown) {
                binding.defaultProgress.visibility = View.GONE
            } else {
                binding.defaultProgress.visibility = View.VISIBLE
            }
        } else {
            if (binding.loadMoreProgress.isShown) {
                binding.loadMoreProgress.visibility = View.GONE
            } else {
                binding.loadMoreProgress.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    private fun openSettings(){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    
    private fun doDeconnexion(){
        val editor = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE).edit()

        editor.putString("my_name", null)
        editor.putString("my_email", null)
        editor.putString("my_token", null)

        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                true
            }
            R.id.deconnexion ->{
                doDeconnexion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}