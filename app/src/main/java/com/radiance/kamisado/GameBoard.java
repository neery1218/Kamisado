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
    private int boardDimension = 8, counter = 0, currColor = -1;
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
                p1Turn(convertedX,convertedY);
            }
            else{
                p2Turn(convertedX,convertedY);
            }
        }
        return true;
    }

    public void p1Turn(int x, int y){
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (p1[i].getX() == x && p1[i].getY() == y && (currColor == -1 || currColor == p1[i].getColor())) {
                    pieceSelected = true;
                    temp = p1[i];
                    break;
                }
            }
        }
        else{
            if(movable(x, y) && !blocked(x, y)){
                temp.setLoc(x, y);
                invalidate();
                invalidate();
                pieceSelected = false;
                counter++;
                currColor = board.board8Color[x][y];
            }
        }
    }

    public void p2Turn(int x, int y){
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (p2[i].getX() == x && p2[i].getY() == y && (currColor == -1 || currColor == p2[i].getColor())) {
                    pieceSelected = true;
                    temp = p2[i];
                    break;
                }
            }
        }
        else{
            if(movable(x, y) && !blocked(x, y)){
                temp.setLoc(x, y);
                invalidate();
                invalidate();
                pieceSelected = false;
                counter++;
                currColor = board.board8Color[x][y];
            }
        }
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

    private boolean blocked(int x, int y){

        int sx = x < temp.getX() ? x : temp.getX(), ex = x > temp.getX() ? x : temp.getX(), sy = y < temp.getY() ? y : temp.getY(), ey = y > temp.getY() ? y : temp.getY();

        for(int i = sx, j = sy; i <= ex && j <= ey; i++, j++){
            for(int k = 0; k < boardDimension; k++){
                if(i != temp.getX() && j != temp.getY())
                if((p1[k].getX() == i && p1[k].getY() == j) || (p2[k].getX() == i && p2[k].getY() == j)){
                    Log.d("TAG", sx + " " + sy + " " + ex + " " + ey + " " + i + " " + j);
                    return true;
                }
            }
        }

        if(sx == ex)
        for(int i = sy; i <= ey; i++){
            for(int j = 0; j < boardDimension; j++){
                if(i != temp.getY())
                if((p1[j].getY() == i && p1[j].getX() == sx) || (p2[j].getY() == i && p2[j].getX() == sx)) {
                    Log.d("TAG", " true");
                    return true;
                }
            }

        }
        return false;
    }
}