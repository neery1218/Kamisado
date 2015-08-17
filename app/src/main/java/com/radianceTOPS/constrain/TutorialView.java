package com.radianceTOPS.constrain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Michael on 7/24/2015.
 */
public class TutorialView extends View{

    private float startX = -1;
    private float endX = -1;
    private float startY = -1;
    private float endY = -1;
    private float width = -1;
    private float height = -1;
    private float borderWidth = 0;
    private float unitSize = 0;
    private int[] playerColor = {Color.parseColor("#090404"), Color.parseColor("#ffecf0f1")};
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;
    private boolean firstTime = true;
    //score array
    private int boardDimension = 8;

    private Paint paint;
    private Board board;

    public TutorialView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        paint = new Paint();
        paint.setTextSize(90);
        board = new Board();
    }

    public void setup(){
        //Only ran once when the view is first created
        if(!firstTime)
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

    public void setBoard(Board board){
        this.board = board;
    }

    private void drawBoard(Canvas canvas){
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                paint.setColor(board.getColor(i, j));
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        paint.setAntiAlias(true);
        setup();
        drawBoard(canvas);
    }
}
