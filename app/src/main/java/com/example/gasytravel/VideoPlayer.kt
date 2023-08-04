package com.example.gasytravel

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView

private const val videoUrl = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [VideoPlayer.newInstance] factory method to
 * create an instance of this fragment.
 */
class VideoPlayer : Fragment() {
    lateinit var videoView: VideoView

    // on below line we are creating
    // a variable for our video url.
    var videoUrl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            videoUrl = it.getString(videoUrl).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video_player, container, false)
        videoView = view.findViewById(R.id.videoView);
        val uri: Uri = Uri.parse(videoUrl)
        videoView.setVideoURI(uri)
        val mediaController = MediaController(view.context)
        mediaController.setAnchorView(videoView)
        mediaController.setMediaPlayer(videoView)
        videoView.setMediaController(mediaController)
        videoView.start()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(urlFromParent: String) =
            VideoPlayer().apply {
                arguments = Bundle().apply {
                    putString(videoUrl, urlFromParent)
                }
            }
    }
}