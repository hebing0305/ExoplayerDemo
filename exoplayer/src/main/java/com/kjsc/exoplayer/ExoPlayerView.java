package com.kjsc.exoplayer;

import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
import static com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class ExoPlayerView extends PlayerView implements DefaultLifecycleObserver {
    ExoPlayer player;
    Uri uri = null;
    List<MediaItem> mediaItems = null;
    @Player.RepeatMode
    int repeatMode = Player.REPEAT_MODE_ONE;

    public ExoPlayerView(Context context) {
        this(context, null, 0);
    }

    public ExoPlayerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExoPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.BLACK);
        setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        if (getUseController()) {
            setFocusable(true);
            setFocusableInTouchMode(true);
        }
    }

    public void initPlayer() {
        if (player == null) {
            //修改默认的缓存时间 因为机顶盒配置过低 缓存过大会OOM
            DefaultLoadControl defaultLoadControl = new DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                            5000,
                            10000,
                            DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                            DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                    )
                    .build();
            player = new ExoPlayer.Builder(getContext())
                    .setLoadControl(defaultLoadControl)
                    .build();
            player.setPlayWhenReady(true);
            if (listener != null) {
                player.addListener(listener);
            }
            player.setRepeatMode(repeatMode);
            setPlayer(player);
        }
    }

    public void setVideoURI(Uri uri) {
        System.out.println("setVideoURI uri=" + uri.toString());
        stop();
        initPlayer();
        this.uri = uri;
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
        player.play();
    }

    public void prepare(Uri uri) {
        System.out.println("setVideoURI uri=" + uri.toString());
        stop();
        initPlayer();
        this.uri = uri;
        player.setPlayWhenReady(false);
        player.setMediaItem(MediaItem.fromUri(uri));
        player.prepare();
    }

    public void play() {
        player.play();
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setMediaItems(List<MediaItem> mediaItems, int startIndex) {
        stop();
        initPlayer();
        this.mediaItems = mediaItems;
        if (mediaItems != null && mediaItems.size() > 0) {
            player.setMediaItems(mediaItems, startIndex, 0);
            player.prepare();
            player.play();
        }
    }


    public void setMediaItems(List<MediaItem> mediaItems) {
        setMediaItems(mediaItems, 0);
    }

    public void setIMediaItems(List<? extends IMediaItem> iMediaItems) {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (IMediaItem iMediaItem : iMediaItems) {
            mediaItems.add(iMediaItem.getMediaItem());
        }
        setMediaItems(mediaItems);
    }

    public void setIMediaItems(List<? extends IMediaItem> iMediaItems, int startIndex) {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (IMediaItem iMediaItem : iMediaItems) {
            mediaItems.add(iMediaItem.getMediaItem());
        }
        setMediaItems(mediaItems, startIndex);
    }

    public void setMediaItemsIndex(int index) {
        if (player != null && index >= 0 && index < mediaItems.size()) {
            player.seekToDefaultPosition(index);
        }
    }

    public int getMediaItemsIndex() {
        if (player != null) {
            return player.getCurrentWindowIndex();
        }
        return -1;
    }

    public void setRepeatMode(@Player.RepeatMode int repeatMode) {
        this.repeatMode = repeatMode;
        if (player != null) {
            player.setRepeatMode(repeatMode);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && getUseController()) {
//            if(isFull && event.getKeyCode()==KeyEvent.KEYCODE_BACK){
//                stopFull();
//                return true;
//            }
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    View btnPlay = findViewById(R.id.exo_play);
                    View btnPause = findViewById(R.id.exo_pause);
                    if (btnPlay.isShown())
                        btnPlay.performClick();
                    else
                        btnPause.performClick();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    Player.Listener listener;

    public void addListener(Player.Listener listener) {
        this.listener = listener;
        if (player != null) {
            player.addListener(listener);
        }
    }

    public void stop() {
        try {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 让控件跟随生命周期
     *
     * @param lifecycle
     */
    public void setLifecycle(Lifecycle lifecycle) {
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            lifecycle.addObserver(this);
        }
    }

    int resumeIndex = 0;

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        super.onResume();
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        super.onResume();
        System.out.println("OnLifecycleEvent ON_RESUME " + uri + " isResumePlay=" + isResumePlay);
        if (isResumePlay && player == null) {
            if (mediaItems != null) {
                setMediaItems(mediaItems, resumeIndex);
            } else if (uri != null) {
                setVideoURI(uri);
            }
            isResumePlay = false;
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        isResumePlay = true;
        System.out.println("OnLifecycleEvent ON_PAUSE " + uri);
        resumeIndex = getMediaItemsIndex();
        stop();
    }

    public ExoPlayer getPlayer() {
        return player;
    }


    boolean isResumePlay = false;

    public boolean isResumePlay() {
        return isResumePlay;
    }

    public void setResumePlay(boolean resumePlay) {
        isResumePlay = resumePlay;
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }

    public <T> ArrayList<T> getMediaItemsData() {
        ArrayList<T> list = new ArrayList<>();
        for (MediaItem mediaItem : mediaItems) {
            list.add((T) mediaItem.localConfiguration.tag);
        }
        return list;
    }

    public Uri getUri() {
        return uri;
    }

//    ViewGroup parent=null;
//    int index=0;
//    boolean isFull=false;
//    View oldFocusView=null;
//    public void full(){
//        parent= (ViewGroup) getParent();
//        index=parent.indexOfChild(this);
//        parent.removeView(this);
//        Activity activity = scanForActivity(getContext());
//        ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        contentView.addView(this, params);
//        setUseController(true);
//        oldFocusView=contentView.findFocus();
//
//        setFocusable(true);
//        requestFocus();
//        isFull=true;
//    }
//
//    public void stopFull(){
//        isFull=false;
//        setUseController(false);
//        if(oldFocusView!=null){
//            oldFocusView.requestFocus();
//            oldFocusView=null;
//        }
//        setFocusable(false);
//
//        Activity activity = scanForActivity(getContext());
//        ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
//        contentView.removeView(this);
//        parent.addView(this,index);
//
//        parent=null;
//    }
}
