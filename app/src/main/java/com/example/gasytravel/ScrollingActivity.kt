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
import android.util.Log
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.service.ApiClient
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
    private var apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        initViews()

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
                        loadPageList()
                    }
                }
            }
        })
        loadPageList()
    }

    private fun loadPageList() {
        toogleLoading()
        val oldCount = posts.size
        val handler = Handler()
        val timeoutInMillis : Long = 5
//        handler.postDelayed({
//            totalAvailablePages = 5
//            val temp = mutableListOf<TvShow>()
//            temp.add(TvShow(1, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(2, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(3, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(4, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(5, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(6, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(1, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(1, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(1, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            temp.add(TvShow(1, "Show 1", "Ongoing", "https://example.com/show1_thumbnail.jpg"))
//            tvShowList.addAll(temp)
//            mainActivityAdapter.updateList(tvShowList, oldCount, tvShowList.size)
//            isLoadMore = false
//            toogleLoading()
//        }, timeoutInMillis)
        apiClient.callGetPosts(currentPage, object : Callback<GetPostsModel> {
            override fun onResponse(call: Call<GetPostsModel>, response: Response<GetPostsModel>) {
                if (response.isSuccessful) {
                    val getPostModel = response.body()
                    if (getPostModel != null) {
                        val oldCount = posts.size
                        totalAvailablePages = getPostModel.maxPage
                        posts.addAll(getPostModel.docs)
                        mainActivityAdapter.updateList(posts, oldCount, posts.size)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}