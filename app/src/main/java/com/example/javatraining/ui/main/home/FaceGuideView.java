package com.example.javatraining.ui.main.home;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class FaceGuideView extends View {

    private Paint paintCircle;
    private Paint paintFeature;
    private Paint paintBackground;
    
    // State variables
    private float smileAmount = 0f; // 0 to 1
    private float eyeOpenAmount = 1f; // 0 to 1
    private float lookDirection = 0f; // -1 (left) to 1 (right)
    
    private ValueAnimator currentAnimator;

    public FaceGuideView(Context context) {
        super(context);
        init();
    }

    public FaceGuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(Color.parseColor("#33FFFFFF"));
        
        paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintCircle.setColor(Color.WHITE);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(8f);
        
        paintFeature = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFeature.setColor(Color.WHITE);
        paintFeature.setStyle(Paint.Style.STROKE);
        paintFeature.setStrokeCap(Paint.Cap.ROUND);
        paintFeature.setStrokeWidth(8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int w = getWidth();
        int h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f;
        float radius = Math.min(w, h) / 2f - 10f;
        
        // Background Circle
        canvas.drawCircle(cx, cy, radius, paintBackground);
        canvas.drawCircle(cx, cy, radius, paintCircle);
        
        // Apply look direction offset
        float eyeOffsetX = lookDirection * (radius * 0.3f);
        
        // Eyes
        float eyeY = cy - radius * 0.2f;
        float eyeDist = radius * 0.4f;
        float leftEyeX = cx - eyeDist / 2f + eyeOffsetX;
        float rightEyeX = cx + eyeDist / 2f + eyeOffsetX;
        
        float eyeWidth = radius * 0.15f;
        float eyeHeight = radius * 0.15f * eyeOpenAmount;
        
        if (eyeOpenAmount < 0.1f) { // Blinked (Line)
            canvas.drawLine(leftEyeX - eyeWidth/2, eyeY, leftEyeX + eyeWidth/2, eyeY, paintFeature);
            canvas.drawLine(rightEyeX - eyeWidth/2, eyeY, rightEyeX + eyeWidth/2, eyeY, paintFeature);
        } else { // Open (Arc/Circle)
            RectF leftEyeRect = new RectF(leftEyeX - eyeWidth/2, eyeY - eyeHeight/2, leftEyeX + eyeWidth/2, eyeY + eyeHeight/2);
            RectF rightEyeRect = new RectF(rightEyeX - eyeWidth/2, eyeY - eyeHeight/2, rightEyeX + eyeWidth/2, eyeY + eyeHeight/2);
            canvas.drawArc(leftEyeRect, 180, 180, false, paintFeature);
            canvas.drawArc(rightEyeRect, 180, 180, false, paintFeature);
        }
        
        // Mouth
        float mouthY = cy + radius * 0.2f;
        float mouthWidth = radius * 0.4f;
        float mouthHeightBase = radius * 0.1f;
        float mouthHeightSmile = radius * 0.4f;
        
        float currentMouthHeight = mouthHeightBase + (mouthHeightSmile - mouthHeightBase) * smileAmount;
        
        RectF mouthRect = new RectF(cx - mouthWidth/2 + eyeOffsetX, mouthY, cx + mouthWidth/2 + eyeOffsetX, mouthY + currentMouthHeight);
        
        if (smileAmount > 0.1f) {
            canvas.drawArc(mouthRect, 0, 180, false, paintFeature);
        } else {
            canvas.drawLine(cx - mouthWidth/2 + eyeOffsetX, mouthY + currentMouthHeight/2, cx + mouthWidth/2 + eyeOffsetX, mouthY + currentMouthHeight/2, paintFeature);
        }
    }
    
    public void animateSmile() {
        resetState();
        currentAnimator = ValueAnimator.ofFloat(0f, 1f);
        currentAnimator.setDuration(800);
        currentAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentAnimator.setRepeatMode(ValueAnimator.REVERSE);
        currentAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        currentAnimator.addUpdateListener(anim -> {
            smileAmount = (float) anim.getAnimatedValue();
            invalidate();
        });
        currentAnimator.start();
    }
    
    public void animateBlink() {
        resetState();
        smileAmount = 0.5f; // Slight smile
        currentAnimator = ValueAnimator.ofFloat(1f, 0f, 1f, 1f, 1f, 1f); // Quick blink, hold open
        currentAnimator.setDuration(2000);
        currentAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentAnimator.addUpdateListener(anim -> {
            eyeOpenAmount = (float) anim.getAnimatedValue();
            invalidate();
        });
        currentAnimator.start();
    }
    
    public void animateLookLeft() {
        resetState();
        smileAmount = 0.2f;
        currentAnimator = ValueAnimator.ofFloat(0f, -1f, -1f, 0f);
        currentAnimator.setDuration(2500);
        currentAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentAnimator.addUpdateListener(anim -> {
            lookDirection = (float) anim.getAnimatedValue();
            invalidate();
        });
        currentAnimator.start();
    }
    
    public void animateLookRight() {
        resetState();
        smileAmount = 0.2f;
        currentAnimator = ValueAnimator.ofFloat(0f, 1f, 1f, 0f);
        currentAnimator.setDuration(2500);
        currentAnimator.setRepeatCount(ValueAnimator.INFINITE);
        currentAnimator.addUpdateListener(anim -> {
            lookDirection = (float) anim.getAnimatedValue();
            invalidate();
        });
        currentAnimator.start();
    }
    
    public void resetState() {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        smileAmount = 0f;
        eyeOpenAmount = 1f;
        lookDirection = 0f;
        invalidate();
    }
}
