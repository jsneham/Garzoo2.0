package com.garzoopvt.garzoo.ImageVideoSlider;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;


public class PagerScroller extends Scroller {
    private int mDuration;

    public PagerScroller(Context context, Interpolator interpolator, int duration) {
        super(context, interpolator);
        mDuration = duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
