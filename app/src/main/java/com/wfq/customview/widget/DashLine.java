package com.wfq.customview.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wfq.customview.R;

/**
 * 自定义虚线
 * 支持水平/垂直方向
 * 支持多状态颜色
 *
 * @author wfq
 * created at 2019/7/2
 */
public class DashLine extends View {

    private static final String TAG = "DashLine";

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mCurrentColor;
    private ColorStateList mColorStateList;
    private int mDashGap = 10;
    private int mDashWidth = 10;
    private int mLineWidth = 4;
    private int mOrientation = HORIZONTAL;
    private Paint mPaint = new Paint();

    public DashLine(Context context) {
        this(context, null);
    }

    public DashLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashLine);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.DashLine_android_color:
                    ColorStateList colorStateList = typedArray.getColorStateList(attr);
                    if (colorStateList != null) {
                        mCurrentColor = colorStateList.getColorForState(getDrawableState(), mCurrentColor);
                        mColorStateList = colorStateList;
                    }
                    break;
                case R.styleable.DashLine_dashGap:
                    mDashGap = typedArray.getDimensionPixelSize(attr, mDashGap);
                    break;
                case R.styleable.DashLine_dashWidth:
                    mDashWidth = typedArray.getDimensionPixelSize(attr, mDashWidth);
                    break;
                case R.styleable.DashLine_lineWidth:
                    mLineWidth = typedArray.getDimensionPixelSize(attr, mLineWidth);
                    break;
                case R.styleable.DashLine_android_orientation:
                    int index = typedArray.getInt(attr, -1);
                    if (index >= 0) setOrientation(index);
                    break;
            }
        }
        typedArray.recycle();
        mPaint.setStrokeWidth(mLineWidth);
    }

    public void setColor(@ColorInt int value) {
        mColorStateList = ColorStateList.valueOf(value);
        updateColor();
    }

    public void setColor(@NonNull ColorStateList colors) {
        mColorStateList = colors;
        updateColor();
    }

    public void setOrientation(@IntRange(from = 0, to = 1) int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mColorStateList != null && mColorStateList.isStateful()) {
            updateColor();
        }
    }

    private void updateColor() {
        boolean inval = false;
        final int[] drawableState = getDrawableState();
        int color = mColorStateList.getColorForState(drawableState, 0);
        if (color != mCurrentColor) {
            mCurrentColor = color;
            inval = true;
        }
        if (inval) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int start = 0;
        mPaint.setColor(mCurrentColor);
        if (mOrientation == HORIZONTAL) {
            while (start < width) {
                canvas.drawLine(start, height >> 1, start + mDashWidth, height >> 1, mPaint);
                start += (mDashGap + mDashWidth);
            }
        } else {
            while (start < height) {
                canvas.drawLine(width >> 1, start, width >> 1, start + mDashWidth, mPaint);
                start += (mDashGap + mDashWidth);
            }
        }
    }
}
