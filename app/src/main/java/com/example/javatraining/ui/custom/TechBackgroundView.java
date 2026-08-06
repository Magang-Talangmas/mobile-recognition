package com.example.javatraining.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class TechBackgroundView extends View {
    private Paint dotPaint;
    private Paint scannerPaint;
    private float scannerY = 0;
    private boolean movingDown = true;

    public TechBackgroundView(Context context) {
        super(context);
        init();
    }

    public TechBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TechBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.parseColor("#334155")); // Slate 700
        
        scannerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();

        // Draw tech dots grid
        int spacing = 80;
        for (int i = 0; i < width; i += spacing) {
            for (int j = 0; j < height; j += spacing) {
                canvas.drawCircle(i, j, 3, dotPaint);
            }
        }

        // Setup gradient for scanner line if not done
        if (scannerPaint.getShader() == null) {
            scannerPaint.setShader(new LinearGradient(0, 0, 0, 40, 
                new int[]{Color.TRANSPARENT, Color.parseColor("#00E5FF"), Color.TRANSPARENT}, 
                null, Shader.TileMode.CLAMP));
        }

        // Draw glowing scanner line
        canvas.drawRect(0, scannerY - 20, width, scannerY + 20, scannerPaint);

        // Update position
        if (movingDown) {
            scannerY += 4; // speed
            if (scannerY > height) movingDown = false;
        } else {
            scannerY -= 4;
            if (scannerY < 0) movingDown = true;
        }

        // Trigger next frame
        invalidate();
    }
}
