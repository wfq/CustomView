package com.wfq.customview.widget;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.wfq.customview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wfq
 * created at 2019/6/27
 */
public class RoundRippleButton extends AppCompatButton {

    private static final String TAG = "RoundRippleButton";

    private Paint mPaint;
    private Point mCenterPoint;
    private ValueAnimator mAnim;
    private int mMaxRadius;
    private int mRippleCount;
    private int mContentRadius;
    private boolean mAutoStart = true;
    private ColorStateList mRippleColor;
    private int mCurrentColor;

    private int mOffset;

    private List<Ripple> mRippleList = new ArrayList<>();


    public RoundRippleButton(Context context) {
        this(context, null);
    }

    public RoundRippleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRippleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundRippleButton);
        Integer duration = null;
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.RoundRippleButton_rippleCount:
                    mRippleCount = typedArray.getInt(attr, 0);
                    break;
                case R.styleable.RoundRippleButton_duration:
                    duration = typedArray.getInt(attr, 0);
                    break;
                case R.styleable.RoundRippleButton_autoStart:
                    mAutoStart = typedArray.getBoolean(attr, true);
                    break;
                case R.styleable.RoundRippleButton_rippleColor:
                    mRippleColor = typedArray.getColorStateList(attr);
                    break;
            }
        }
        typedArray.recycle();

        mCenterPoint = new Point();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mAnim = new ValueAnimator();
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        if (duration != null) {
            mAnim.setDuration(duration);
        }
        mAnim.setInterpolator(new LinearInterpolator());
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) mAnim.getAnimatedValue();
                refreshRipples();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        mCenterPoint.set(width / 2, height / 2);
        mMaxRadius = Math.min(width, height) / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int maxPadding = Math.max(Math.max(paddingLeft, paddingRight), Math.max(paddingTop, paddingBottom));
        mContentRadius = mMaxRadius - maxPadding;
        mAnim.setIntValues(mContentRadius, mMaxRadius);
        initRipples();
        if (mAutoStart) {
            start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mCurrentColor);
        for (Ripple ripple : mRippleList) {
            mPaint.setAlpha(ripple.getAlpha());
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, ripple.getRadius(), mPaint);
        }
        mPaint.setAlpha(255);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mContentRadius, mPaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mRippleColor != null && mRippleColor.isStateful()) {
            updateColor();
        }
    }

    public RoundRippleButton setAutoStart(boolean isAuto) {
        mAutoStart = isAuto;
        return this;
    }

    public void setRippleColor(@ColorInt int color) {
        mRippleColor = ColorStateList.valueOf(color);
        updateColor();
    }

    public void setRippleColor(@NonNull ColorStateList colors) {
        mRippleColor = colors;
        updateColor();
    }

    public void setRippleCount(int count) {
        mRippleCount = count;
        initRipples();
    }

    public RoundRippleButton setInterpolator(TimeInterpolator value) {
        if (value == null) {
            value = new LinearInterpolator();
        }
        mAnim.setInterpolator(value);
        return this;
    }

    private void initRipples() {
        mRippleList.clear();
        if (mMaxRadius == 0) return;
        int maxRippleRadius = mMaxRadius - mContentRadius;
        for (int i = 0; i < mRippleCount; i++) {
            int offset = maxRippleRadius * i / mRippleCount;
            Ripple ripple = new Ripple(mContentRadius, mMaxRadius);
            ripple.setOffset(offset);
            mRippleList.add(ripple);
        }
    }

    private void refreshRipples() {
        if (mRippleList.isEmpty()) return;
        for (Ripple ripple : mRippleList) {
            ripple.setRadius(mOffset);
        }
        invalidate();
    }

    private void updateColor() {
        boolean inval = false;
        final int[] drawableState = getDrawableState();
        int color = mRippleColor.getColorForState(drawableState, 0);
        if (color != mCurrentColor) {
            mCurrentColor = color;
            inval = true;
        }
        if (inval) {
            invalidate();
        }
    }

    public void start() {
        if (mAnim.isStarted()) return;
        if (mRippleList.isEmpty()) return;
        mAnim.start();
    }

    public void stop() {
        if (!mAnim.isStarted()) return;
        mAnim.end();
    }

    public boolean isRunning() {
        return mAnim.isRunning();
    }

    public RoundRippleButton setDuration(long duration) {
        mAnim.setDuration(duration);
        return this;
    }

    private static class Ripple {

        private int minRadius;
        private int maxRadius;

        private int mAlpha;
        private int mOffset;
        private int mRadius;

        Ripple(int minRadius, int maxRadius) {
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
        }

        void setRange(int minRadius, int maxRadius) {
            this.minRadius = minRadius;
            this.maxRadius = maxRadius;
        }

        int getOffset() {
            return mOffset;
        }

        void setOffset(int mOffset) {
            this.mOffset = mOffset;
        }

        int getRadius() {
            return mRadius;
        }

        void setRadius(int radius) {
            int diff = radius - mOffset;
            if (diff < minRadius) {
                mRadius = maxRadius + diff - minRadius;
            } else {
                mRadius = diff;
            }
            setAlpha();
        }

        int getAlpha() {
            return mAlpha;
        }

        private void setAlpha() {
            mAlpha = 204 * (maxRadius - mRadius) / (maxRadius - minRadius);
        }
    }
}
