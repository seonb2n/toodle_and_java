package com.origincurly.toodletoodle.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import com.origincurly.toodletoodle.R;

public class CustomSeekBar extends AppCompatSeekBar {

    private Paint mRulerPaint;
    //tick mark 개수
    private int mRulerCount =  12;
    //tick mark 두께
    private int mRulerWidth = 3;

    //tick mark 높이
    private int mRulerHeight = 15;
    //tick mark 색깔
    private int mRulerColor = ContextCompat.getColor(getContext(), R.color.tick_mark_color);
    private boolean isShowTopOfThumb = false;

    public CustomSeekBar(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRulerPaint = new Paint();
        mRulerPaint.setColor(mRulerColor);
        mRulerPaint.setAntiAlias(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSplitTrack(false);
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() <= 0 || mRulerCount <= 0) {
            return;
        }

        int length = (getWidth() - getPaddingLeft() - getPaddingRight() - mRulerCount * mRulerWidth) / (mRulerCount + 1);
        int rulerTop = getHeight() / 2 - getMinimumHeight() / 2 / 3;
        int rulerBottom = rulerTop + getMinimumHeight() / 3;

        //Get the position information of the slider
        Rect thumbRect = null;
        if (getThumb() != null) {
            thumbRect = getThumb().getBounds();
        }

        for (int i = 1; i <= mRulerCount; i++) {
            //Calculate the left and right coordinates of the tick marks
            int rulerLeft = i * length + getPaddingLeft();
            int rulerRight = rulerLeft + mRulerWidth;

            //Determine whether the scale line needs to be drawn
            if (!isShowTopOfThumb && thumbRect != null && rulerLeft - getPaddingLeft() > thumbRect.left && rulerRight - getPaddingLeft() < thumbRect.right) {
                continue;
            }

            //Draw up
            canvas.drawRect(rulerLeft, rulerTop, rulerRight, rulerBottom, mRulerPaint);
        }
    }

    public void setRulerCount(int mRulerCount) {
        this.mRulerCount = mRulerCount;
        requestLayout();
    }

    public void setRulerWidth(int mRulerWidth) {
        this.mRulerWidth = mRulerWidth;
        requestLayout();
    }

    public void setRulerColor(int mRulerColor) {
        this.mRulerColor = mRulerColor;
        if (mRulerPaint != null) {
            mRulerPaint.setColor(mRulerColor);
            requestLayout();
        }
    }

    public void setShowTopOfThumb(boolean isShowTopOfThumb) {
        this.isShowTopOfThumb = isShowTopOfThumb;
        requestLayout();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
    }
}
