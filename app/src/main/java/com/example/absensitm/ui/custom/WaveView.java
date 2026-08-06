package com.example.absensitm.ui.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaveView extends View {

    private Paint wavePaint1;
    private Paint wavePaint2;
    private Path wavePath1;
    private Path wavePath2;

    private float waveShift1 = 0f;
    private float waveShift2 = 0f;
    private ValueAnimator animator;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        wavePath1 = new Path();
        wavePath2 = new Path();

        wavePaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint1.setColor(Color.parseColor("#D8E2F2")); // Light blue
        wavePaint1.setStyle(Paint.Style.FILL);

        wavePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint2.setColor(Color.parseColor("#0A2C87")); // Dark blue
        wavePaint2.setStyle(Paint.Style.FILL);

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(4000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            waveShift1 = fraction * getWidth();
            // To loop seamlessly, the shift must equal the wavelength (which is 1.2 * width for wave2)
            waveShift2 = fraction * getWidth() * 1.2f; 
            invalidate();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animator != null && !animator.isRunning()) {
            animator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        // Wave characteristics
        float waveLength = width;
        float waveHeight1 = height * 0.15f;
        float waveHeight2 = height * 0.2f;
        float baseHeight1 = height * 0.4f;
        float baseHeight2 = height * 0.5f;

        wavePath1.reset();
        wavePath2.reset();

        wavePath1.moveTo(0, height);
        wavePath2.moveTo(0, height);
        wavePath1.lineTo(0, baseHeight1);
        wavePath2.lineTo(0, baseHeight2);

        // Draw light blue wave (wave1)
        for (float x = 0; x <= width; x += 10) {
            float y = (float) (baseHeight1 + waveHeight1 * Math.sin((x + waveShift1) * 2 * Math.PI / waveLength));
            wavePath1.lineTo(x, y);
        }
        wavePath1.lineTo(width, height);
        wavePath1.close();

        // Draw dark blue wave (wave2)
        for (float x = 0; x <= width; x += 10) {
            float y = (float) (baseHeight2 + waveHeight2 * Math.sin((x + waveShift2) * 2 * Math.PI / (waveLength * 1.2)));
            wavePath2.lineTo(x, y);
        }
        wavePath2.lineTo(width, height);
        wavePath2.close();

        canvas.drawPath(wavePath1, wavePaint1);
        canvas.drawPath(wavePath2, wavePaint2);
    }
}
