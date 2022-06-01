package com.kjsc.exoplayer;

import android.graphics.Rect;

public class RectBean {
    Rect rect = new Rect();
    long start;
    long end;

    public RectBean(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
