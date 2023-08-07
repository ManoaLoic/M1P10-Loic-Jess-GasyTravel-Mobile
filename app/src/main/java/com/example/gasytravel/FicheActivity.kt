package com.example.gasytravel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.gasytravel.databinding.ActivityFicheBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.gasytravel.databinding.ContentFicheBinding
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide


class FicheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFicheBinding
    private lateinit var contentBinding: ContentFicheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityFicheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contentBinding = ContentFicheBinding.inflate(layoutInflater)
        binding.container.addView(contentBinding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val postId = intent.getStringExtra("id")

        val apiClient = ApiClient(this)
        apiClient.callGetPostDetails(postId, object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        contentBinding.textViewDestinationName.text = post.titre
                        contentBinding.textViewDescription.text =HtmlCompat.fromHtml(post.description, HtmlCompat.FROM_HTML_MODE_COMPACT)

                        contentBinding.textViewPriceAndUnit.text = "${post.prix} ${post.unite}"

                        Glide
                            .with(this@FicheActivity)
                            .load(post.brand)
                            .centerCrop()
                            .placeholder(R.drawable.loading)
                            .into(contentBinding.imageViewDestination)

                        val videoButton: Button = contentBinding.video
                        if(post.video.isNullOrBlank()) {
                            videoButton.isEnabled = false
                            videoButton.text = "Pas de video disponible"
                        }
                        videoButton.setOnClickListener {
                            val intent = Intent(this@FicheActivity, Video::class.java)
                            intent.putExtra("videoUrl", post.video)
                            this@FicheActivity.startActivity(intent)
                        }

                    }
                } else {
                    Log.e("FicheActivity", "Error fetching post details: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("FicheActivity", "API call failed: ${t.message}")
            }
        })

        binding.fab.setOnClickListener { view ->
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

