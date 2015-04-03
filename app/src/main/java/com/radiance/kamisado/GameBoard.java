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
    private Board board = new Board(this);
    private boolean firstTime = true, pieceSelected = false;
    private float clickX, clickY;
    private int boardDimension = 8, counter = 0;
    private Piece[] p1 = new Piece[boardDimension], p2 = new Piece[boardDimension];
    private Piece temp;

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
        startX = borderWidth; endX = width - borderWidth; unitSize = (endX - startX) / boardDimension;
        startY = height - (height - width) / 2 - width + borderWidth; endX = height - (height - width) / 2 + borderWidth;


        int[][] temp = new int[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                temp[i][j] = board.board8Color[j][i];
            }
        }

        board.board8Color = temp;

        for(int i = 0; i < boardDimension; i++){
            p1[i] = new Piece(i, 0, board.board8Color[i][0]);
            p2[i] = new Piece(i, boardDimension - 1, board.board8Color[i][boardDimension - 1]);
        }

        Log.d("TAG", "called");
    }

    public void drawBoard(Canvas c){
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                paint.setColor(board.board8Color[i][j]);
                c.drawRect(startX + i * unitSize, startY + j * unitSize, startX + (i + 1) * unitSize, startY + (j + 1) * unitSize, paint);
            }
        }
    }

    public void drawPiece(Canvas canvas){
        for(int i = 0; i < boardDimension; i++){
            paint.setColor(Color.WHITE);
            canvas.drawCircle(startX + p1[i].getX() * unitSize + unitSize / 2, startY + unitSize * p1[i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(p1[i].getColor());
            canvas.drawCircle(startX + p1[i].getX() * unitSize + unitSize / 2, startY + unitSize * p1[i].getY() + unitSize / 2, unitSize / 3, paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(startX + p2[i].getX() * unitSize + unitSize / 2, startY + unitSize * p2[i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(p2[i].getColor());
            canvas.drawCircle(startX + p2[i].getX() * unitSize + unitSize / 2, startY + unitSize * p2[i].getY() + unitSize / 2, unitSize / 3, paint);
        }
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        int e = event.getAction();
        if(e == 1){
            float x = event.getX(), y = event.getY();
            int convertedX = (int)((x - startX) / unitSize), convertedY = (int)((y - startY) / unitSize);
            if(counter % 2 == 0) {
                if(!pieceSelected) {
                    for (int i = 0; i < boardDimension; i++) {
                        if (p1[i].getX() == convertedX && p1[i].getY() == convertedY) {
                            pieceSelected = true;
                            temp = p1[i];
                            Log.d("TAG", "selected");
                            break;
                        }
                    }
                }
                else{
                    if(movable(convertedX, convertedY)){
                        temp.setLoc(convertedX, convertedY);
                        invalidate();
                        invalidate();
                        pieceSelected = false;
                        counter++;
                        Log.d("TAG", temp.getX() + " " + temp.getY() + "moved");
                    }
                }
            }
            else{
                if(!pieceSelected) {
                    for (int i = 0; i < boardDimension; i++) {
                        if (p2[i].getX() == convertedX && p2[i].getY() == convertedY) {
                            pieceSelected = true;
                            temp = p2[i];
                            Log.d("TAG", "selected");
                            break;
                        }
                    }
                }
                else{
                    if(movable(convertedX, convertedY)){
                        temp.setLoc(convertedX, convertedY);
                        invalidate();
                        invalidate();
                        pieceSelected = false;
                        counter++;
                        Log.d("TAG", temp.getX() + " " + temp.getY() + "moved");
                    }
                }
            }
        }
        return true;
    }

    private boolean movable(int x, int y){

        if(counter % 2 == 0 && temp.getY() > y)
            return false;
        else if(counter % 2 == 1 && temp.getY() < y)
            return false;

        if(x == temp.getX() && y == temp.getY())
            return  false;

        if(temp.getX() == x){
            return true;
        }
        else if((int)Math.abs(temp.getX() - x) == (int)Math.abs(temp.getY() - y)){
            return true;
        }
        return false;
    }
}