package com.kjsc.exoplayer

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView


class ExoPlayerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(
        context, attrs, defStyleAttr
    ), DefaultLifecycleObserver {

    var player: ExoPlayer? = null
    var playerView: PlayerView
    var controller: CustomPlayerControlView
    var progressBar: CustomProgressBar


    var uri: Uri? = null
    private var mediaItems: List<MediaItem>? = null
    var repeatMode = Player.REPEAT_MODE_ONE
        set(value) {
            field = value
            player?.repeatMode = repeatMode
        }

    init {
        playerView = PlayerView(context, attrs, defStyleAttr)
        playerView.setPadding(0, 0, 0, 0)
        playerView.isFocusable = false
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(playerView, params)
        controller = findViewById(R.id.exo_controller)
        progressBar = findViewById(R.id.exo_progress)
        setBackgroundColor(Color.BLACK)
        playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
        if (playerView.useController) {
            isFocusable = true
            isFocusableInTouchMode = true
        }
    }

    fun initPlayer() {
        destroy()
        //修改默认的缓存时间 因为机顶盒配置过低 缓存过大会OOM
        val defaultLoadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                5000,
                10000,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
        player = ExoPlayer.Builder(context)
            .setLoadControl(defaultLoadControl)
            .build()
        player?.playWhenReady = true
        if (listener != null) {
            player?.addListener(listener!!)
        }
        player?.repeatMode = repeatMode
        playerView.setPlayer(player)
    }

    fun setVideoURI(uri: Uri, startPositionMs: Long = 0) {
        println("setVideoURI uri=$uri")
        initPlayer()
        this.uri = uri
        player?.setMediaItem(MediaItem.fromUri(uri), startPositionMs)
        player?.prepare()
        player?.play()
    }

    fun prepare(uri: Uri) {
        println("setVideoURI uri=$uri")
        initPlayer()
        this.uri = uri
        player?.playWhenReady = false
        player?.setMediaItem(MediaItem.fromUri(uri))
        player?.prepare()
    }

    fun play() {
        player?.play()
    }

    fun setVideoPath(path: String?) {
        setVideoURI(Uri.parse(path))
    }

    fun setMediaItems(mediaItems: List<MediaItem>?, startIndex: Int = 0, startPos: Long = 0) {
        initPlayer()
        this.mediaItems = mediaItems
        if (mediaItems != null && mediaItems.isNotEmpty()) {
            player?.setMediaItems(mediaItems, startIndex, startPos)
            player?.prepare()
            player?.play()
        }
    }

    fun getMediaItems(): List<MediaItem>? {
        return mediaItems
    }

    fun setIMediaItems(iMediaItems: List<IMediaItem>, startIndex: Int = 0, startPos: Long = 0) {
        val mediaItems: MutableList<MediaItem> = ArrayList()
        for (iMediaItem in iMediaItems) {
            mediaItems.add(iMediaItem.mediaItem)
        }
        setMediaItems(mediaItems, startIndex, startPos)
    }

    var mediaItemsIndex: Int
        get() = player?.currentMediaItemIndex ?: -1
        set(index) {
            if (player != null && index >= 0 && index < mediaItems!!.size) {
                player?.seekToDefaultPosition(index)
            }
        }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && playerView.useController) {
            if (playerView.useController) {
                controller.show()
                val ret1 = controller.dispatchKeyEvent(event)
                val ret2 = progressBar.onKeyDown(event.keyCode, event)
                if (ret1 || ret2) {
                    return true
                }
            }
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                if (playerView.parent != this) {
                    exitFullScreen()
                    return true
                }
            }
        }

        return super.dispatchKeyEvent(event)
    }

    fun isFullScreen(): Boolean {
        return playerView.parent != this
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return super.onKeyDown(keyCode, event)
    }

    var listener: Player.Listener? = null
    fun addListener(listener: Player.Listener?) {
        this.listener = listener
        if (player != null) {
            player?.addListener(listener!!)
        }
    }

    fun destroy() {
        try {
            player?.stop()
            player?.release()
            player = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 让控件跟随生命周期
     *
     * @param lifecycle
     */
    fun setLifecycle(lifecycle: Lifecycle?) {
        if (lifecycle != null) {
            lifecycle.removeObserver(this)
            lifecycle.addObserver(this)
        }
    }

    var resumeIndex = 0 //记录上次播放的视频下标
    var resumePosition = 0L //记录上次播放的视频位置
    var isResumePlay = false //是否需要恢复播放
    override fun onPause(owner: LifecycleOwner) {
        playerView.onPause()
        player?.let {
            isResumePlay = true
            resumeIndex = mediaItemsIndex
            resumePosition = it.currentPosition
        }
        destroy()
    }

    override fun onResume(owner: LifecycleOwner) {
        playerView.onResume()
        println("OnLifecycleEvent ON_RESUME $uri isResumePlay=$isResumePlay")
        if (isResumePlay && player == null) {
            if (mediaItems != null) {
                setMediaItems(mediaItems, resumeIndex, resumePosition)
            } else if (uri != null) {
                setVideoURI(uri!!, resumePosition)
            }
            isResumePlay = false
        }
    }

    fun <T> getMediaItemsData(): ArrayList<T?> {
        val list = ArrayList<T?>()
        for (mediaItem in mediaItems!!) {
            list.add(mediaItem.localConfiguration!!.tag as T?)
        }
        return list
    }

    fun fullScreen() {
        val activity = getActivityFromContext(context)
        val viewGroup = activity?.window?.decorView as? ViewGroup
        if (viewGroup != null) {
            removeView(playerView)
            val params =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            viewGroup.addView(playerView, params)
            playerView.useController = true
        }
    }

    fun exitFullScreen() {
        val activity = getActivityFromContext(context)
        val viewGroup = activity?.window?.decorView as? ViewGroup
        if (viewGroup != null) {
            viewGroup.removeView(playerView)
            val params =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(playerView, params)
            playerView.useController = false
        }
    }

    fun getActivityFromContext(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        if (context is Activity) {
            return context
        }
        if (context is Application || context is Service) {
            return null
        }
        var c = context
        while (c != null) {
            if (c is ContextWrapper) {
                c = c.baseContext
                if (c is Activity) {
                    return c
                }
            } else {
                return null
            }
        }
        return null
    }

}