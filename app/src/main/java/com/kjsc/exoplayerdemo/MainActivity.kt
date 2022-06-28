package com.kjsc.exoplayerdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kjsc.exoplayer.ExoPlayerView

class MainActivity : FragmentActivity() {
    lateinit var exoplayer: ExoPlayerView
    var playIndex = 0
    var isError = false
    lateinit var channelList: List<Channel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val json = getJsonForAssets("channellist.json")
        channelList = Gson().fromListJson<List<Channel>>(json)
        exoplayer = findViewById(R.id.exoplayer)
        exoplayer.setLifecycle(lifecycle)
        exoplayer.setIMediaItems(channelList)
        exoplayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                isError = true
                showToast("播放出错！" + error.message)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                isError = false
                val channel = mediaItem?.localConfiguration?.tag as? Channel
                playIndex = channelList.indexOf(channel)
                showToast("正在播放：" + channel?.channelName)
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (playIndex > 0) {
                    playIndex--
                    exoplayer.setIMediaItems(channelList, playIndex)
                } else {
                    showToast("已经是第一个频道了")
                }
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (playIndex < channelList.size) {
                    playIndex++
                    exoplayer.setIMediaItems(channelList, playIndex)
                } else {
                    showToast("已经是最后一个频道了")
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}


fun Context.getJsonForAssets(fileName: String): String {
    var inputStream = assets.open(fileName)
    var byteArray = ByteArray(inputStream.available())
    inputStream.read(byteArray)
    inputStream.close()
    return String(byteArray)
}

inline fun <reified T> Gson.fromListJson(json: String): T {
    return fromJson(json, object : TypeToken<T>() {}.type)
}

var toastWeak: Toast? = null
fun Context.showToast(msg: String) {
    try {
        Handler(Looper.getMainLooper()).post {
            toastWeak?.cancel()
            toastWeak = Toast.makeText(
                this,
                msg,
                Toast.LENGTH_SHORT
            )
            toastWeak?.show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}