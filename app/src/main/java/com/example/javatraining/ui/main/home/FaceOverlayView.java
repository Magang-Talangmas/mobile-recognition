package com.example.javatraining.ui.main.home;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

public class FaceOverlayView extends View {
    private Paint backgroundPaint;
    private Paint transparentPaint;
    private Path path;

    public FaceOverlayView(Context context) {
        super(context);
        init();
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#B3000000"));
        backgroundPaint.setStyle(Paint.Style.FILL);

        transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw dark background
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // Calculate center and radius
        float cx = width / 2f;
        float cy = height / 2f;
        
        // Match the 300dp size from xml (150dp radius)
        float density = getResources().getDisplayMetrics().density;
        float radius = 150 * density;

        // Draw transparent circle cutout
        canvas.drawCircle(cx, cy, radius, transparentPaint);
    }
}
