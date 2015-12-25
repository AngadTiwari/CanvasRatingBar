package com.angtwr31.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Angad Tiwari on 12/22/15.
 */
public class CanvasRatingBar extends View {

    private Paint fillPaint;
    private Paint blankPaint;
    private Path[] polyPath;

    private float startAngle = 270.0f;
    private int points = 5;

    private int maxStars;
    private String fillColor;
    private int starMargin;

    private Context mContext;
    private TypedArray typedArray;

    private boolean halfStarEnable;
    private float ratingValue;

    private float[] xPt;
    private float[] yPt;
    private Rect rectCustomRatingBar;
    private Rect[] starRectBound;
    private static DisplayMetrics displayMetrics;

    private int xOffset, yOffset;
    private int xOffsetFixed, yOffsetFixed;

    public CanvasRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        this.mContext = context;
        typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CanvasRatingBar, 0, 0);
        displayMetrics = mContext.getResources().getDisplayMetrics();

        fillPaint = new Paint();
        fillPaint.setColor(typedArray.getColor(R.styleable.CanvasRatingBar_fillColor,
                getResources().getColor(android.R.color.holo_red_dark)));
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        blankPaint = new Paint();
        blankPaint.setColor(typedArray.getColor(R.styleable.CanvasRatingBar_fillColor,
                getResources().getColor(android.R.color.holo_red_dark)));
        blankPaint.setAntiAlias(true);
        blankPaint.setStyle(Paint.Style.STROKE);

        maxStars = typedArray.getInteger(R.styleable.CanvasRatingBar_maxStars, 5);
        starMargin = typedArray.getInteger(R.styleable.CanvasRatingBar_starMargin, 0);
        halfStarEnable = typedArray.getBoolean(R.styleable.CanvasRatingBar_halfStarEnable, false);
        ratingValue = typedArray.getFloat(R.styleable.CanvasRatingBar_ratingValue, 2.5f);

        typedArray.recycle();
    }

    @Override
    public void draw(Canvas canvas) {

        drawStarsUptoRatingValue(canvas);
        drawRestStars(canvas);
        calculateBounds(canvas);

        super.draw(canvas);
    }

    public void calculateBounds(Canvas canvas)
    {
        int[] locations = new int[2];
        getLocationOnScreen(locations);

        rectCustomRatingBar=canvas.getClipBounds();
        rectCustomRatingBar.left  += locations[0];
        rectCustomRatingBar.top +=locations[1];
        rectCustomRatingBar.right  += locations[0];
        rectCustomRatingBar.bottom +=locations[1];

        starRectBound=new Rect[maxStars];

        int xDiff= rectCustomRatingBar.right - rectCustomRatingBar.left;
        int yDiff= rectCustomRatingBar.bottom - rectCustomRatingBar.top;

        rectCustomRatingBar.left  += yDiff;
        rectCustomRatingBar.right  += yDiff;

        int starWidth = xDiff/maxStars;
        int starHeight = yDiff;

        xOffsetFixed = locations[0] +yDiff ;
        yOffsetFixed = locations[1];

        xOffset = locations[0] +yDiff ;
        yOffset = locations[1];

        for(int i=0;i<maxStars;i++)
        {
            starRectBound[i] = new Rect();

            starRectBound[i].left = xOffset;
            starRectBound[i].top = rectCustomRatingBar.top;
            starRectBound[i].bottom = rectCustomRatingBar.bottom;
            starRectBound[i].right = xOffset;
            xOffset= xOffset + starWidth;
            starRectBound[i].right = xOffset;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch(action) {
            case MotionEvent.ACTION_MOVE : {
                float i=1;

                int xPrecise=(int)(dipToPixels(event.getXPrecision() * event.getX())) + xOffsetFixed;
                int yPrecise = (int) event.getY() + yOffsetFixed;

                Log.d("ACTION_MOVE", "(x,y) = ("+xPrecise+","+yPrecise+")");
                for(Rect rect: starRectBound)
                {
                    boolean isTouchInsideThisBarBound = rect.contains(xPrecise, yPrecise);

                    if(isTouchInsideThisBarBound==true)
                    {
                        Rect firstHalfRect=new Rect();
                        Rect secondHalfRect=new Rect();

                        /**
                         * if rect hit with firstHalfRect subtract 0.5 to i
                         * else if rect hit with secondHalfRect do nothing
                         */
                        firstHalfRect.left = rect.left;
                        firstHalfRect.top = rect.top;
                        firstHalfRect.right = rect.left + ((rect.right - rect.left)/2);
                        firstHalfRect.bottom = rect.bottom;

                        secondHalfRect.left = rect.left + ((rect.right - rect.left)/2);
                        secondHalfRect.top = rect.top;
                        secondHalfRect.right = rect.right;
                        secondHalfRect.bottom = rect.bottom;

                        if(firstHalfRect.contains(xPrecise, yPrecise))
                            i -= 0.5;
                        else if(secondHalfRect.contains(xPrecise, yPrecise))
                            i = i;

                        Log.d("ACTION_INTERCEPT", "Intercept found on rect("+i+")");
                        ratingValue= i;
                        invalidate();
                        break;
                    }
                    else if(starRectBound.length == i && isTouchInsideThisBarBound==false)
                    {
                        Log.d("ACTION_INTERCEPT", "Intercept found on rect(0)");
                        ratingValue= 0.0f;
                        invalidate();
                        break;
                    }
                    i++;
                }

                break;
            }
            case MotionEvent.ACTION_DOWN : {
                Log.d("ACTION_DOWN", "(x,y) = ("+event.getX()+","+event.getY()+")");

                break;
            }
        }

        invalidate();
        return true;
    }

    public void drawStarsUptoRatingValue(Canvas canvas)
    {
        xPt=new float[maxStars*2];
        yPt=new float[maxStars*2];
        polyPath=new Path[maxStars];

        if(ratingValue< 0.0)
            ratingValue=0.0f;
        if(ratingValue>maxStars)
            ratingValue= maxStars;

        boolean isHalfStar=(ratingValue- (int)ratingValue) == 0.5;

        for(int k=1;k<= (isHalfStar ? (int)ratingValue+1: (int)ratingValue );k++)
        {
            polyPath[k-1] = new Path();

            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            xPt[0] = (k*getMeasuredHeight())+starMargin;
            yPt[0] = (measuredHeight / 2);

            int outerRadius = (int)Math.min(xPt[0], yPt[0]);
            int innerRadius = outerRadius / 2;

            float a = (float) (Math.PI * 2) / (5 * 2);
            int workingRadius = outerRadius;
            polyPath[k-1].reset();

            canvas.save();
            canvas.translate(xPt[0], yPt[0]);
            polyPath[k-1].moveTo(workingRadius, 0);
            for (int i = 1; i < points * 2; i++) {
                workingRadius = (workingRadius == outerRadius) ? innerRadius : outerRadius;
                xPt[i] = (float) (workingRadius * Math.cos(a * i));
                yPt[i] = (float) (workingRadius * Math.sin(a * i));
            }

            /**
             * to draw first half of the star with fill
             */
            for(int i=9; i>= ((k == (int)ratingValue+1)  && halfStarEnable && (ratingValue - (int)ratingValue)==0.5f ? 5 : 1) ; i--)
            {
                polyPath[k-1].lineTo(xPt[i], yPt[i]);
            }
            polyPath[k-1].close();
            canvas.rotate(startAngle);
            canvas.drawPath(polyPath[k - 1], fillPaint);

            /**
             * to draw second half of the star without fill
             */
            if(false)//(k == (int)ratingValue+1)  && halfStarEnable && (ratingValue - (int)ratingValue)==0.5f)
            {
                Path tempPolyPath=new Path();
                tempPolyPath.reset();

                canvas.save();
                canvas.translate(xPt[0], yPt[0]);

                tempPolyPath.lineTo(xPt[0], yPt[0]);

                for(int z=5;z<=9;z++)
                {
                    tempPolyPath.lineTo(xPt[z], yPt[z]);
                }
                tempPolyPath.close();
                canvas.rotate(startAngle);
                canvas.drawPath(tempPolyPath, blankPaint);
            }

            canvas.restore();
        }
    }

    public void drawRestStars(Canvas canvas)
    {
        for(int k=(int)ratingValue+1;k<= maxStars;k++)
        {
            polyPath[k-1] = new Path();

            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            xPt[0] = (k*getMeasuredHeight())+starMargin;
            yPt[0] = (measuredHeight / 2);

            int outerRadius = (int)Math.min(xPt[0], yPt[0]);
            int innerRadius = outerRadius / 2;

            float a = (float) (Math.PI * 2) / (5 * 2);
            int workingRadius = outerRadius;
            polyPath[k-1].reset();

            canvas.save();
            canvas.translate(xPt[0], yPt[0]);
            polyPath[k-1].moveTo(workingRadius, 0);
            for (int i = 1; i < points * 2; i++) {
                workingRadius = (workingRadius == outerRadius) ? innerRadius : outerRadius;
                xPt[i] = (float) (workingRadius * Math.cos(a * i));
                yPt[i] = (float) (workingRadius * Math.sin(a * i));
            }

            for(int i=9; i>= 1 ; i--)
            {
                polyPath[k-1].lineTo(xPt[i], yPt[i]);
            }

            polyPath[k-1].close();
            canvas.rotate(startAngle);
            canvas.drawPath(polyPath[k-1], blankPaint);

            canvas.restore();
        }
    }

    public static float dipToPixels(float dipValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, displayMetrics);
    }

    public static float pixelsToDip(float pixelValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, displayMetrics);
    }
}
