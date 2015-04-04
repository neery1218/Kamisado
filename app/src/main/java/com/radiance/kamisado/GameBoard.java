package com.radiance.kamisado;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by neerajen on 31/03/15.
 */
public class GameBoard extends View {

    private Paint paint;
    private float startX = -1, endX = -1, startY = -1, endY = -1, width = -1, height = -1, borderWidth = 0, unitSize = 0;
    private Board board = new Board(this);
    private boolean firstTime = true, firstMove = true, pieceSelected = false;
    private int boardDimension = 8, counter = 1, currColor = -1;
    private Piece[] p1 = new Piece[boardDimension], p2 = new Piece[boardDimension];
    private Piece selectedPiece;
    private int PLAYER_ONE = 0;
    private int PLAYER_TWO = 1;
    private int[] score;
    private ArrayList<Point> availMoves;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        score = new int[2];

        invalidate();
    }

    public void setup(Canvas canvas){
        if(!firstTime)
            return;

        firstTime = false;

        width = getWidth();
        height = getHeight();

        startX = borderWidth;
        endX = width - borderWidth;

        unitSize = (endX - startX) / boardDimension;

        startY = height - (height - width) / 2 - width + borderWidth;
        endX = height - (height - width) / 2 + borderWidth;

        for(int i = 0; i < boardDimension; i++){
            p1[i] = new Piece(i, 0, board.board8Color[i][0]);
            p2[i] = new Piece(i, boardDimension - 1, board.board8Color[i][boardDimension - 1]);
        }
    }//intialisation of the gameboard

    public void drawBoard(Canvas c){//draws the board
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                paint.setColor(board.board8Color[i][j]);
                c.drawRect(startX + i * unitSize, startY + j * unitSize, startX + (i + 1) * unitSize, startY + (j + 1) * unitSize, paint);
            }
        }
    }

    public void drawPiece(Canvas canvas){//draws all pieces on the game board
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

    private int win (){//checks if a player has won
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            if (p1[i].getY() == boardDimension - 1) {
                score[PLAYER_ONE]++;
                return PLAYER_ONE;
            }
            if (p2[i].getY() == 0) {
                score[PLAYER_TWO]++;
                return PLAYER_TWO;
            }
        }
        //check for player two
        return -1;
    }

    private void displayMoves(Canvas canvas, int x, int y){//displays all possible moves for the user
        ArrayList<Point> availMoves = new ArrayList<Point>();
        int[][] board = new int[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                board[i][j] = 0;
            }
        }
        for(int i = 0; i < boardDimension; i++){
            board[p1[i].getX()][p1[i].getY()] = 1;
            board[p2[i].getX()][p2[i].getY()] = 1;
        }
        if(counter % 2 == 0){
            availMoves = searchP1(x,y,availMoves,board);
        }
        else{
            availMoves = searchP2(x,y,availMoves,board);
        }
        for(int i = 0; i < availMoves.size(); i++){
            Point p = availMoves.get(i);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(150);
            canvas.drawRect(startX + p.x * unitSize, startY + p.y * unitSize, startX + (p.x + 1) * unitSize, startY + (p.y + 1) * unitSize, paint);
        }
        this.availMoves = availMoves;
    }

    private ArrayList<Point> searchP1(int x, int y, ArrayList<Point> availMoves, int[][] board){
        for(int i = y + 1   ; i < boardDimension; i++){
            if(board[x][i] == 0)
                availMoves.add(new Point(x, i));
            else
                break;
        }
        for(int i = x + 1, j = y + 1; i < boardDimension && j < boardDimension; i++, j++){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                break;
            }
        }
        for(int i = x - 1, j = y + 1; i >= 0 && j < boardDimension; i--, j++){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                break;
            }
    }
        return availMoves;
    }

    private ArrayList<Point> searchP2(int x, int y, ArrayList<Point> availMoves, int[][] board){
        for(int i = y - 1; i >= 0; i--){
            if(board[x][i] == 0)
                availMoves.add(new Point(x, i));
            else
                break;
        }
        for(int i = x + 1, j = y - 1; i < boardDimension && j >= 0; i++, j--){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                break;
            }
        }
        for(int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                break;
            }
        }
        return availMoves;
    }

    public void p1Turn(int x, int y){
        if(availMoves.size() == 0){
            counter++;
            for(int j = 0; j < boardDimension; j++){
                if(p2[j].getColor() == currColor){
                    selectedPiece = p2[j];
                    invalidate();
                }
            }
        }
        for(int i = 0; i < availMoves.size(); i++){
            Point temp = availMoves.get(i);
            if(temp.x == x && temp.y == y){
                selectedPiece.setLoc(x, y);
                counter++;
                currColor = board.board8Color[x][y];//next piece color
                for(int j = 0; j < boardDimension; j++){
                    if(p2[j].getColor() == currColor){
                        selectedPiece = p2[j];
                        invalidate();
                    }
                }
                break;
            }
        }
    }

    public void p2Turn(int x, int y){
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (p2[i].getX() == x && p2[i].getY() == y) {
                    pieceSelected = true;
                    selectedPiece = p2[i];
                    invalidate();
                    break;
                }
            }
        }
        else{
            if(firstMove){
                for(int i = 0; i < boardDimension; i++){
                    if(p2[i].getX() == x && p2[i].getY() == y){
                        selectedPiece = p2[i];
                        invalidate();
                        break;
                    }
                }

            }
            if(availMoves.size() == 0){
                counter++;
                for(int j = 0; j < boardDimension; j++){
                    if(p1[j].getColor() == currColor){
                        selectedPiece = p1[j];
                        Log.d("TAG", p1[j].toString());
                        invalidate();
                    }
                }
            }
            for(int i = 0; i < availMoves.size(); i++){
                Point temp = availMoves.get(i);
                if(temp.x == x && temp.y == y){
                    firstMove = false;
                    selectedPiece.setLoc(x, y);
                    counter++;
                    currColor = board.board8Color[x][y];//next piece color
                    for(int j = 0; j < boardDimension; j++){
                        if(p1[j].getColor() == currColor){
                            selectedPiece = p1[j];
                            invalidate();
                        }
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        if(selectedPiece != null)
            displayMoves(canvas, selectedPiece.getX(), selectedPiece.getY());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int e = event.getAction();
        if(e == 1){
            float x = event.getX(), y = event.getY();
            int convertedX = (int)((x - startX) / unitSize), convertedY = (int)((y - startY) / unitSize);//converts the passed coordinates into a location on the board
            if(counter % 2 == PLAYER_ONE) {//determines turn
                p1Turn(convertedX,convertedY);
            }
            else{
                p2Turn(convertedX,convertedY);
            }

            if (win() != -1){//if a player has won
                Log.v("WIN","WIN");
            }
        }
        return true;
    }
}