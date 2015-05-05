package com.radiance.kamisado;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by neerajen on 04/05/15.
 */
public class introBoardView extends View {
    private Paint paint;

    private float startX = -1;
    private float endX = -1;
    private float startY = -1;
    private float endY = -1;
    private float width = -1;
    private float height = -1;
    private float borderWidth = 0;
    private float unitSize = 0;
    private boolean firstTime = true;
    private int boardDimension = 8;
    private int[] colors = {Color.parseColor("#ffe74c3c"), //red
            Color.parseColor("#d35400"), //orange
            Color.parseColor("#F7CA18"),//yellow
            Color.parseColor("#ff2ecc71"),//green
            Color.parseColor("#ff3498db"),//blue
            Color.parseColor("#ff8e44ad"),//purple
            Color.parseColor("#D2527F"),//pink
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];
    private int[][] boardColor = new int[][]{
            {o, b, p, pk, ye, r, g, br},
            {r, o, pk, g, b, ye, br, p},
            {g, pk, o, r, p, br, ye, b},
            {pk, p, b, o, br, g, r, ye},
            {ye, r, g, br, o, b, p, pk},
            {b, ye, br, p, r, o, pk, g},
            {p, br, ye, b, g, pk, o, r},
            {br, g, r, ye, pk, p, b, o}};


    public introBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

    }


    public void setup() {
        //Only ran once when the view is first created
        if (!firstTime)
            return;

        firstTime = false;

        //Sets up the width and height of the gameControl on the screen
        //The gameControl is centered in the screen with a possible border around them
        width = getWidth();
        height = getHeight();

        //Finding the start and end point of the gameControl with border included
        startX = borderWidth;
        endX = width - borderWidth;

        //The size of each individual square on the gameControl
        unitSize = (endX - startX) / boardDimension;

        //Finding the start and end point along the vertical axis
        //Calculated by subtracting half the remainder of height with the gameControl accounted for and then subtracting the gameControl height for start point
        //End point is same thing except without subtracting the gameControl width
        startY = height - (height - width) / 2 - width + borderWidth;
        endX = height - (height - width) / 2 + borderWidth;

        //Creates the piece objects

    }//initialisation of the gameboard

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setup();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                paint.setColor(boardColor[i][j]);
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);
                paint.setColor(getResources().getColor(R.color.white));
                paint.setAlpha(200);
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);


            }
        }
        paint.setColor(Color.BLACK);
    }
}
