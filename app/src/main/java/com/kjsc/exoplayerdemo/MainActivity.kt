package com.kjsc.exoplayerdemo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.kjsc.exoplayer.ExoPlayerView

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val exoplayer = findViewById<ExoPlayerView>(R.id.exoplayer)
        exoplayer.setVideoPath("http://vfx.mtime.cn/Video/2021/07/10/mp4/210710171112971120.mp4")
        exoplayer.setLifecycle(lifecycle)
    }
}