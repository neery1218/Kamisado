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
    private float startX = -1, endX = -1, startY = -1, endY = -1, width = -1, height = -1, borderWidth = 0, unitSize = 0;
    private int[] colors = {Color.RED, Color.parseColor("#ED872D"), Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.parseColor("#69359C"), Color.parseColor("#FFB7C5"),
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];
    private Board board = new Board(this);
    private boolean firstTime = true, draw = false;
    private float clickX, clickY;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);

        invalidate();
    }

    public void setup(Canvas canvas){
        if(!firstTime)
            return;
        firstTime = false;
        width = getWidth(); height = getHeight();
        startX = borderWidth; endX = width - borderWidth; unitSize = (endX - startX) / 8;
        startY = height - (height - width) / 2 - width + borderWidth; endX = height - (height - width) / 2 + borderWidth;
        board.boardColor = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};
        board.boardPiece = new int[8][8];

        int[][] temp = new int[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                temp[i][j] = board.boardColor[j][i];
            }
        }

        board.boardColor = temp;
        Log.d("TAG", "called");
    }

    public void drawBoard(Canvas c){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                paint.setColor(board.boardColor[i][j]);
                c.drawRect(startX + i * unitSize, startY + j * unitSize, startX + (i + 1) * unitSize, startY + (j + 1) * unitSize, paint);
            }
        }
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);

        if(draw){
            draw = false;
            canvas.drawRect(clickX * unitSize + startX, clickY * unitSize + startY, (clickX + 1) * unitSize + startX, (clickY + 1) * unitSize + startY, paint);
            Log.d("TAG", "" + clickX + " " + clickY);
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        int e = event.getAction();
        if(e == 1){
            float x = event.getX(), y = event.getY(), convertedX = (int)((x - startX) / unitSize), convertedY = (int)((y - startY) / unitSize);
            draw = true;
            clickX = convertedX; clickY = convertedY;
            invalidate();
        }
        return true;
    }
}