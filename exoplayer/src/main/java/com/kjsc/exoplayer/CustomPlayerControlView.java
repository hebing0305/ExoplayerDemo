package com.kjsc.exoplayer;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ui.PlayerControlView;

public class CustomPlayerControlView extends PlayerControlView {


    public CustomPlayerControlView(Context context) {
        super(context);
    }

    public CustomPlayerControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPlayerControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomPlayerControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, @Nullable AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr, playbackAttrs);
    }

    @Override
    public void hide() {
        super.hide();
        if (onVisibilityChangeListener != null) {
            onVisibilityChangeListener.onVisibilityChange(false);
        }
    }

    @Override
    public void show() {
        super.show();
        if (onVisibilityChangeListener != null) {
            onVisibilityChangeListener.onVisibilityChange(true);
        }
    }

    onVisibilityChangeListener onVisibilityChangeListener;

    public void setOnVisibilityChangeListener(CustomPlayerControlView.onVisibilityChangeListener onVisibilityChangeListener) {
        this.onVisibilityChangeListener = onVisibilityChangeListener;
    }

    public interface onVisibilityChangeListener {
        void onVisibilityChange(boolean isShow);
    }
}
