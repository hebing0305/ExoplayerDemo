package com.kjsc.exoplayerdemo

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaItem.fromUri
import com.kjsc.exoplayer.IMediaItem

data class Channel(
    val channelName: String,
    val channelNum: Int,
    val channelUrl: String
) : IMediaItem {
    override fun getMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri("http://vfx.mtime.cn/Video/2021/07/10/mp4/210710171112971120.mp4")
            .setTag(this)
            .build()
    }
}