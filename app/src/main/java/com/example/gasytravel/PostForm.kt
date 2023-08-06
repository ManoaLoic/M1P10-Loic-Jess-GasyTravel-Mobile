package com.example.gasytravel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.gasytravel.databinding.ActivityFicheBinding
import com.example.gasytravel.databinding.ActivityPostFormBinding
import com.example.gasytravel.model.GetPostsModel
import com.example.gasytravel.model.Post
import com.example.gasytravel.model.UploadBodyModel
import com.example.gasytravel.model.UploadResponseModel
import com.example.gasytravel.service.ApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File


class PostForm : AppCompatActivity() {

    private lateinit var binding: ActivityPostFormBinding
    private var apiClient = ApiClient(this)
    private var imgName : String = ""
    private var videoPath : String = ""
    private var videoName : String = ""
    private var downloadUrl : String = ""
    private var videoUrl : String = ""
    private var videoAdded : Boolean = false

    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 101
    private val REQUEST_CODE_PICK_VIDEO = 102

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
            checkPermissionsAndOpenGallery("IMAGE")
        }

        val selectVideoButton: Button = findViewById(R.id.ajout_video)
        selectVideoButton.setOnClickListener {
            checkPermissionsAndOpenGallery("VIDEO")
        }

        val posterButton: Button = findViewById(R.id.poster)
        posterButton.setOnClickListener {
            toogleLoading()
            uploadFile()
            if(videoAdded) uploadVideo()
        }

        if (savedInstanceState == null) {
            val fragment = RichText.newInstance("", "")
            supportFragmentManager.beginTransaction()
                .replace(R.id.richText, fragment)
                .commit()
        }

    }

    private val openGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { videoUri ->
                    val projection = arrayOf(
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME
                    )
                    contentResolver.query(videoUri, projection, null, null, null)?.use { cursor ->
                        val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                        val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                        cursor.moveToFirst()
                        videoPath = cursor.getString(pathIndex)
                        videoName = cursor.getString(nameIndex)
                        Log.e("DEBUG", "Path = $videoPath , Name = $videoName")
                        val videoText : TextView = binding.video
                        videoText.text = videoName
                        videoText.visibility = View.VISIBLE
                        videoAdded = true
                    }
                }
            }
    }

    fun getVideoAsBase64(videoFileName: String): String? {
        val file = File(videoFileName)
        if (!file.exists() || !file.isFile) {
            return null
        }

        val buffer = ByteArray(file.length().toInt())
        val inputStream = file.inputStream()
        inputStream.read(buffer)
        inputStream.close()

        return Base64.encodeToString(buffer, Base64.DEFAULT)
    }

    private fun uploadVideo() {
        val base64String : String? = getVideoAsBase64(videoPath)
        if (base64String != null) {
            println("Video Base64: $base64String")

            val uploadBody: UploadBodyModel = UploadBodyModel(
                name = videoName,
                file = base64String
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
                            videoUrl = response.downloadURL
                            createPost()
                        }
                    }
                }
            })

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
        Log.e("DEBUG", "Create post : $videoUrl ")
        Log.e("DEBUG", "Create post : $downloadUrl ")
        if(videoAdded && videoUrl  == ""){
            return
        }

        val titre : String = findViewById<EditText>(R.id.Titre).text.toString()
        val inputPrix : EditText = findViewById<EditText>(R.id.Prix)
        var prix : Double? = 0.00
        if(inputPrix?.text?.toString() != null && inputPrix?.text?.toString() != ""){
            prix = inputPrix.text?.toString()?.toDouble()
        }
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
            video = videoUrl,
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
                    val intent = Intent(this@PostForm, FicheActivity::class.java)
                     intent.putExtra("id", response.body()?.id)
                    this@PostForm.startActivity(intent)
                    toogleLoading()
                }
            }
        })
    }

    private fun checkPermissionsAndOpenGallery(type : String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery(type)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            openGallery(type)
        }
    }

    private fun openGallery(type : String) {
        if(type == "VIDEO"){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            openGalleryLauncher.launch(intent)
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(galleryIntent)
        }
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