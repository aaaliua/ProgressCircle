package com.alokvnair.progresscircle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Alok Nair
 */
public class ProgressCircle extends View {

    private static final int POINTS_COUNT = 9;
    private static final int STEP = 3;
    private static final int BIG_STEP = 1;
    private static final int DEFAULT_COLOR = Color.rgb(87, 247, 250);
    private static final float DEFAULT_RADIUS = 180;
    private static final float DEFAULT_CIRCLE_RADIUS = 40;
    private static final float DEFAULT_MOVE_RADIUS = 30;
    private static final int SPLIT_ANGLE = 45;
    private static final int ADDITION_LENGTH = 6;
    private static final int FLAT_ANGLE = 180;

    /**
     * for save and restore instance of view.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String ANGLE = "angle";
    private static final String BIGCIRCLECENTERX = "bigCircleCenterX";
    private static final String BIGCIRCLECENTERY = "bigCircleCenterY";
    private static final String RADIUS = "radius";
    private static final String CIRCLERADIUS = "circleRadius";
    private static final String MOVERADIUS = "moveRadius";
    private static final String POINTCOLOR = "pointColor";
    private static final String STARTX1 = "startX1";
    private static final String STARTY1 = "startY1";
    private static final String ENDX1 = "endX1";
    private static final String ENDY1 = "endY1";
    private static final String STARTX2 = "startX2";
    private static final String STARTY2 = "startY2";
    private static final String ENDX2 = "endX2";
    private static final String ENDY2 = "endY2";
    private static final String CONTROLX1 = "controlX1";
    private static final String CONTROLY1 = "controlY1";
    private static final String BIGSTEP = "bigStep";

    private CirclePoint[] circlePoints = new CirclePoint[POINTS_COUNT];

    private int angle = 0;
    private int bigStep = BIG_STEP;
    float bigCircleCenterX;
    float bigCircleCenterY;
    float radius;
    float circleRadius;
    float moveRadius;
    int pointColor;

    private float startX1;
    private float startY1;
    private float startX2;
    private float startY2;

    private float controlX1;
    private float controlY1;

    private float endX1;
    private float endY1;
    private float endX2;
    private float endY2;

    private Path path1;
    private Paint circlePaint;
    private Paint linePaint;

    private boolean isFirst = true;

    public ProgressCircle(Context context) {
        this(context, null);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressCircle,
                defStyleAttr, 0);
        pointColor = attributes.getColor(R.styleable.ProgressCircle_point_color, DEFAULT_COLOR);
        boolean isDynamic = attributes.getBoolean(R.styleable.ProgressCircle_isdynamic, true);
        isDynamic(isDynamic);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFirst) {
            init();
            isFirst = false;
        }
        for (int i = 0; i < POINTS_COUNT - 1; i ++) {
            CirclePoint p = circlePoints[i];
            p.x = getPaddingLeft() + bigCircleCenterX + (float)Math.cos(Math.toRadians(p.currentAngle)) * radius;
            p.y = getPaddingTop() + bigCircleCenterY + (float)Math.sin(Math.toRadians(p.currentAngle)) * radius;
            p.currentAngle = p.currentAngle + bigStep;
            canvas.drawCircle(p.x, p.y, p.radius, circlePaint);
        }
        calculateMovingPoint(canvas);
        angle = angle + STEP;
        invalidate();
    }

    protected void init() {
        float temp = getHeight() > getWidth() ? getWidth() / 2 : getHeight() / 2;
        radius = temp - temp / DEFAULT_RADIUS * DEFAULT_CIRCLE_RADIUS;
        circleRadius = DEFAULT_CIRCLE_RADIUS / DEFAULT_RADIUS * radius;
        moveRadius = DEFAULT_MOVE_RADIUS / DEFAULT_RADIUS * radius;
        bigCircleCenterX = getPaddingLeft() + getWidth() / 2;
        bigCircleCenterY = getPaddingTop() + getHeight() / 2;

        path1 = new Path();
        initializePaints();
        initializePoints();
    }

    /**
     * Calculate the dynamic point
     * @param canvas
     */
    protected void calculateMovingPoint(Canvas canvas) {
        CirclePoint p = circlePoints[POINTS_COUNT - 1];
        p.x = bigCircleCenterX + (float)Math.cos(Math.toRadians(angle)) * radius;
        p.y = bigCircleCenterY + (float)Math.sin(Math.toRadians(angle)) * radius;
        canvas.drawCircle(p.x, p.y, p.radius, circlePaint);
        for (int i = 0; i < POINTS_COUNT - 1; i++) {
            CirclePoint biggerP1 = circlePoints[i];

            //Intersect
            if (isIntersect(p, biggerP1)) {
                canvas.drawCircle(biggerP1.x, biggerP1.y, biggerP1.radius + ADDITION_LENGTH*(1-getDistanceRatio(p, biggerP1)), circlePaint);
            }

            if (isConnect(p, biggerP1)) {
                float headOffsetX1 = (float)(circleRadius *Math.sin(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float headOffsetY1 = (float)(circleRadius *Math.cos(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float footOffsetX1 = (float)(moveRadius *Math.sin(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float footOffsetY1 = (float)(moveRadius *Math.cos(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));

                startX1 = biggerP1.x - headOffsetX1;
                startY1 = biggerP1.y + headOffsetY1;

                endX1 = biggerP1.x + headOffsetX1;
                endY1 = biggerP1.y - headOffsetY1;

                startX2 = p.x - footOffsetX1;
                startY2 = p.y + footOffsetY1;

                endX2 = p.x + footOffsetX1;
                endY2 = p.y - footOffsetY1;

                controlX1 = (biggerP1.x + p.x) / 2;
                controlY1 = (biggerP1.y + p.y) / 2;

                path1.reset();
                path1.moveTo(startX1, startY1);
                path1.quadTo(controlX1, controlY1, startX2, startY2);
                path1.lineTo(endX2, endY2);
                path1.quadTo(controlX1, controlY1, endX1, endY1);
                path1.lineTo(startX1, startY1);
                canvas.drawPath(path1, linePaint);
            }
        }
    }

    protected void initializePoints() {
        for (int i = 0; i < POINTS_COUNT; i++) {
            CirclePoint p = new CirclePoint();
            p.currentAngle = SPLIT_ANGLE * i;
            p.x = getPaddingLeft() + bigCircleCenterX + (float)Math.cos(Math.toRadians(p.currentAngle)) * radius;
            p.y = getPaddingTop() + bigCircleCenterY + (float)Math.sin(Math.toRadians(p.currentAngle)) * radius;
            p.color = pointColor;
            p.radius = circleRadius;
            if (i == POINTS_COUNT - 1) {
                p.radius = moveRadius;
            }
            circlePoints[i] = p;
        }
    }

    protected void initializePaints() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(pointColor);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(pointColor);

    }

    private boolean isIntersect(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance < (a.radius + b.radius);
    }

    private boolean isConnect(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance < radius * Math.cos(Math.toRadians((FLAT_ANGLE - SPLIT_ANGLE) / 2));
    }

    private float getDistanceRatio(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance / (a.radius + b.radius);
    }

    public void setPointColor(int color) {
        pointColor = color;
        if (linePaint != null) {
            linePaint.setColor(color);
        }
        if (circlePaint != null) {
            circlePaint.setColor(color);
        }

    }

    public void isDynamic(boolean dynamic) {
        if (dynamic) {
            bigStep = BIG_STEP;
        } else {
            bigStep = 0;
        }
    }

    protected float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE,super.onSaveInstanceState());
        bundle.putInt(ANGLE, angle);
        bundle.putFloat(BIGCIRCLECENTERX, bigCircleCenterX);
        bundle.putFloat(BIGCIRCLECENTERY, bigCircleCenterY);
        bundle.putFloat(RADIUS, radius);
        bundle.putFloat(CIRCLERADIUS, circleRadius);
        bundle.putFloat(MOVERADIUS, moveRadius);
        bundle.putFloat(STARTX1, startX1);
        bundle.putFloat(STARTY1, startY1);
        bundle.putFloat(ENDX1, endX1);
        bundle.putFloat(ENDY1, endY1);
        bundle.putFloat(STARTX2, startX2);
        bundle.putFloat(STARTY2, startY2);
        bundle.putFloat(ENDX2, endX2);
        bundle.putFloat(ENDY2, endY2);
        bundle.putFloat(CONTROLX1, controlX1);
        bundle.putFloat(CONTROLY1, controlY1);
        bundle.putInt(POINTCOLOR, pointColor);
        bundle.putInt(BIGSTEP, bigStep);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            final Bundle bundle = (Bundle)state;
            angle = bundle.getInt(ANGLE);
            bigCircleCenterX = bundle.getFloat(BIGCIRCLECENTERX);
            bigCircleCenterY = bundle.getFloat(BIGCIRCLECENTERY);
            radius = bundle.getFloat(RADIUS);
            circleRadius = bundle.getFloat(CIRCLERADIUS);
            moveRadius = bundle.getFloat(MOVERADIUS);
            startX1 = bundle.getFloat(STARTX1);
            startY1 = bundle.getFloat(STARTY1);
            endX1 = bundle.getFloat(ENDX1);
            endY1 = bundle.getFloat(ENDY1);
            startX2 = bundle.getFloat(STARTX2);
            startY2 = bundle.getFloat(STARTY2);
            endX2 = bundle.getFloat(ENDX2);
            endY2 = bundle.getFloat(ENDY2);
            controlX1 = bundle.getFloat(CONTROLX1);
            controlY1 = bundle.getFloat(CONTROLY1);
            pointColor = bundle.getInt(POINTCOLOR);
            bigStep = bundle.getInt(BIGSTEP);
            init();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    static class CirclePoint {
        public int currentAngle;
        public float radius;
        public float x;
        public float y;
        public int color;
    }

}
