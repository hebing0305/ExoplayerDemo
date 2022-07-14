package com.kjsc.exoplayerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.kjsc.exoplayer.ExoPlayerView

class TestActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val exoplayer = findViewById<ExoPlayerView>(R.id.exoplayer)
        exoplayer.setVideoPath("http://vfx.mtime.cn/Video/2021/07/10/mp4/210710171112971120.mp4")
        exoplayer.setOnClickListener {
            exoplayer.fullScreen()
        }
        exoplayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                Log.i("", "playbackState=$playbackState")
                exoplayer.play()
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                error.printStackTrace()
            }
        })
        exoplayer.setOnFocusChangeListener { v, hasFocus ->
            println("setOnFocusChangeListener hasFocus$hasFocus")
        }
        exoplayer.play()
    }
}