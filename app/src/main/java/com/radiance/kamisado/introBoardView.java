package com.radiance.kamisado;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by neerajen on 04/05/15.
 */
public class introBoardView extends View {
    private int[][] boardColor;
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

    public introBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int[] colors = {R.color.red, //red
                R.color.orange, //orange
                R.color.yellow,//yellow
                R.color.green,//green
                R.color.blue,//blue
                R.color.purple,//purple
                R.color.pink,//pink
                R.color.brown};
        int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];


        boardColor = new int[][]{
                {o, b, p, pk, ye, r, g, br},
                {r, o, pk, g, b, ye, br, p},
                {g, pk, o, r, p, br, ye, b},
                {pk, p, b, o, br, g, r, ye},
                {ye, r, g, br, o, b, p, pk},
                {b, ye, br, p, r, o, pk, g},
                {p, br, ye, b, g, pk, o, r},
                {br, g, r, ye, pk, p, b, o}};
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
    public void onDraw(Canvas canvas) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                paint.setColor(boardColor[i][j]);
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);

            }
        }
    }
}
