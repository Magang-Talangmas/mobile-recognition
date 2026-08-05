package com.example.absensitm.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.absensitm.R;

public class ScannerOverlayView extends View {

    private Paint backgroundPaint;
    private Paint transparentPaint;
    private Paint borderPaint;
    private Paint scannerPaint;
    
    private RectF scannerRect;
    private float scannerLineY;
    private ValueAnimator scannerAnimator;

    public ScannerOverlayView(Context context) {
        super(context);
        init();
    }

    public ScannerOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.transparent_overlay));

        transparentPaint = new Paint();
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        borderPaint = new Paint();
        borderPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorSecondary));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8f);
        borderPaint.setAntiAlias(true);

        scannerPaint = new Paint();
        scannerPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorSecondary));
        scannerPaint.setStyle(Paint.Style.FILL);
        scannerPaint.setAntiAlias(true);

        scannerRect = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float width = w * 0.7f;
        float height = width * 1.3f;
        float left = (w - width) / 2;
        float top = (h - height) / 2;
        
        scannerRect.set(left, top, left + width, top + height);
        startScannerAnimation();
    }

    private void startScannerAnimation() {
        if (scannerAnimator != null) {
            scannerAnimator.cancel();
        }

        scannerAnimator = ValueAnimator.ofFloat(scannerRect.top, scannerRect.bottom);
        scannerAnimator.setDuration(2000);
        scannerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scannerAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scannerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scannerAnimator.addUpdateListener(animation -> {
            scannerLineY = (float) animation.getAnimatedValue();
            invalidate();
        });
        scannerAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw background shadow
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
        
        // Draw transparent hole
        float cornerRadius = 32f;
        canvas.drawRoundRect(scannerRect, cornerRadius, cornerRadius, transparentPaint);
        
        // Draw border
        canvas.drawRoundRect(scannerRect, cornerRadius, cornerRadius, borderPaint);
        
        // Draw scanner line
        if (scannerLineY >= scannerRect.top && scannerLineY <= scannerRect.bottom) {
            canvas.drawRect(scannerRect.left + 16, scannerLineY - 4, scannerRect.right - 16, scannerLineY + 4, scannerPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scannerAnimator != null) {
            scannerAnimator.cancel();
        }
    }
}
