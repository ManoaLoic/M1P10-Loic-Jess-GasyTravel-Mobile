package com.example.gasytravel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.example.gasytravel.R
import com.example.gasytravel.model.UserModel
import com.example.gasytravel.service.ApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "Nouveau Post"
    private val NOTIFICATION_ID = 1

    private fun createNotificationChannel(context: Context, title : String?, message : String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = title
            val channelDescription = message
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            channel.description = channelDescription
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, title: String?, message: String?) {
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("DEBUG", "From: ${remoteMessage.from}")

        val ok : Boolean = PreferenceHelper.getRecevoirNotification(this)
        Log.e("DEBUG", "Recevoir notification $ok")
        if(ok){
            if (remoteMessage.data.isNotEmpty()) {
                Log.d("DEBUG", "Message data payload: ${remoteMessage.data}")

                val title = remoteMessage.data["title"]
                val message = remoteMessage.data["message"]

                createNotificationChannel(this, title, message)
                showNotification(this, title, message)
            }

            remoteMessage.notification?.let {
                Log.d("DEBUG", "Message Notification Body: ${it.body}")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        var apiClient = ApiClient(this)
        val sharedPreferences = this.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("my_id", null)
        if(!id.isNullOrBlank()){
            apiClient.fillDeviceToken(UserModel(id, "", "", "", token), object : Callback<UserModel> {
                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("DEBUG", "exception")
                }

                override fun onResponse(
                    call: Call<UserModel>,
                    response: Response<UserModel>
                ) {
                    Log.e("DEBUG", "Refreshed token")
                }
            })
        }

    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }

    object PreferenceHelper {
        private const val PREF_NAME = "your_preference_file_name"

        fun getRecevoirNotification(context: Context): Boolean {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return sharedPreferences.getBoolean("Recevoir notification", true)
        }
    }

}