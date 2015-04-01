package com.radiance.kamisado;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by neerajen on 31/03/15.
 */
public class GameBoard extends View {

    private Paint paint;
    private float x = 100, y = 100;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        canvas.drawRect(0, 0, x, y, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        x = event.getX(); y = event.getY();
        invalidate();
        return true;
    }
}
