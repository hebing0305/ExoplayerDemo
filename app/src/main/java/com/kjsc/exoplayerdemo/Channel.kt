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
            .setUri(channelUrl)
            .setTag(this)
            .build()
    }
}