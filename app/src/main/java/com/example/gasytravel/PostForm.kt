package com.example.gasytravel

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.gasytravel.databinding.ActivityFicheBinding
import com.example.gasytravel.databinding.ActivityPostFormBinding
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.model.UploadBodyModel
import com.example.gasytravel.model.UploadResponseModel
import com.example.gasytravel.service.ApiClient
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class PostForm : AppCompatActivity() {

    private lateinit var binding: ActivityPostFormBinding
    private var apiClient = ApiClient(this)
    private var imgName : String = ""
    private var downloadUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityPostFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val spinner: Spinner = findViewById(R.id.spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.type_post,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val selectImageButton: Button = findViewById(R.id.ajout_image)
        selectImageButton.setOnClickListener {
            openGallery()
        }

        val posterButton: Button = findViewById(R.id.poster)
        posterButton.setOnClickListener {
            toogleLoading()
            uploadFile()
        }

        if (savedInstanceState == null) {
            val fragment = RichText.newInstance("", "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.richText, fragment)
                .commit()
        }

    }

    private fun uploadFile() {
        val imageView = findViewById<ImageView>(R.id.brand_image)
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            val bitmap = drawable.bitmap
            val base64Image: String = encodeImageToBase64(bitmap)
            val uploadBody: UploadBodyModel = UploadBodyModel(
                name = imgName,
                file = base64Image
            )

            apiClient.callUploadFile(uploadBody, object : Callback<UploadResponseModel> {
                override fun onFailure(call: Call<UploadResponseModel>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("DEBUG", "exception", t)
                    toogleLoading()
                }

                override fun onResponse(
                    call: Call<UploadResponseModel>,
                    response: Response<UploadResponseModel>
                ) {
                    if (response.isSuccessful) {
                        val response = response.body()
                        if (response != null) {
                            downloadUrl = response.downloadURL
                            createPost()
                        }
                    }
                }
            })
        }
    }

    private fun toogleLoading(){
        if (binding.loading.isShown) {
            binding.loading.visibility = View.GONE
        } else {
            binding.loading.visibility = View.VISIBLE
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createPost(){
        val titre : String = findViewById<EditText>(R.id.Titre).text.toString()
        val prix : Double = findViewById<EditText>(R.id.Prix).text.toString().toDouble()
        val type : String = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        var description : String = ""
        val richTextFragment = supportFragmentManager.findFragmentById(R.id.richText) as RichText?
        if (richTextFragment != null) {
            // Get the current value from the RichText fragment
            val currentValue = richTextFragment.getCurrentValue()


            if (currentValue != null) {
                description = currentValue
            }
        }
        val post = Post(
            id = null,
            titre = titre,
            description = description,
            prix = prix,
            unite = null,
            brand = downloadUrl,
            type = type
        )
        apiClient.callCreatePost(post, object : Callback<Post> {
            override fun onFailure(call: Call<Post>, t: Throwable) {
                t.printStackTrace()
                Log.e("DEBUG", "exception", t)
                toogleLoading()
            }

            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (response.isSuccessful) {
                    val intent = Intent(this@PostForm, Detail::class.java)
                     intent.putExtra("id", response.body()?.id)
                    this@PostForm.startActivity(intent)
                    toogleLoading()
                }
            }
        })
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(galleryIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data

            // Check if the URI is not null and is of type "content://"
            if (selectedImageUri != null && selectedImageUri.toString().startsWith("content://")) {
                val imageFileName = selectedImageUri.getFileNameFromUri()
                if (imageFileName != null) {
                    imgName = imageFileName
                }

                val imageView: ImageView = findViewById(R.id.brand_image)
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(selectedImageUri)
            }
        }
    }

    // Extension function to get the file name from a content URI
    private fun Uri.getFileNameFromUri(): String? {
        val contentResolver = this@PostForm.contentResolver
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(this, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return it.getString(columnIndex)
            }
        }

        return null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

}