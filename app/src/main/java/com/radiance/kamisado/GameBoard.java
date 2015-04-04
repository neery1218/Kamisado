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
    private boolean firstTime = true, pieceSelected = false;
    private int boardDimension = 8, counter = 0, currColor = -1, cliclX, clickY;
    private Piece[] p1 = new Piece[boardDimension], p2 = new Piece[boardDimension];
    private Piece selectedPiece;
    private boolean firstMove;
    private int PLAYER_ONE = 0;
    private int PLAYER_TWO = 1;
    private int[] score;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLUE);
        firstMove = true;
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

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);
        drawPiece(canvas);
        displayMoves(canvas, cliclX, clickY);
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
        for(int i = 0; i < boardDimension; i++){
            String s = "";
            for(int j = 0; j < boardDimension; j++){
                s += board[i][j];
            }
            Log.d("TAG", s);
        }
        for(int i = 0; i < availMoves.size(); i++){
            Log.d("TAG", availMoves.get(i).toString());
        }
        for(int i = 0; i < availMoves.size(); i++){
            Point p = availMoves.get(i);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(150);
            canvas.drawRect(startX + p.x * unitSize, startY + p.y * unitSize, startX + (p.x + 1) * unitSize, startY + (p.y + 1) * unitSize, paint);
        }
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
                Log.d("TAG", i + " " + j + " " + board[j][i] + " break");
                break;
            }
        }
        for(int i = x - 1, j = y + 1; i >= 0 && j < boardDimension; i--, j++){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                Log.d("TAG", i + " " + j + " " + board[i][j] + " break");
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
                Log.d("TAG", i + " " + j + " " + board[i][j] + " break");
                break;
            }
        }
        for(int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--){
            if(board[i][j] == 0)
                availMoves.add(new Point(i, j));
            else {
                Log.d("TAG", i + " " + j + " " + board[i][j] + " break");
                break;
            }
        }
        return availMoves;
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

    public void p1Turn(int x, int y){
        if(!pieceSelected) {//if a piece hasn't been selected yet, determine if the selected piece is the right one
            for (int i = 0; i < boardDimension; i++) {
                if (p1[i].getX() == x && p1[i].getY() == y && (currColor == -1 || currColor == p1[i].getColor())) {
                    pieceSelected = true;
                    selectedPiece = p1[i];
                    clickY = p1[i].getY();
                    cliclX = p1[i].getX();
                    invalidate();
                    break;
                }
            }
        }
        else{//if it has, check if the move is valid
            if(movable(x, y) && !blocked(x, y)){//if it is, move the piece, call onDraw, increment counter
                selectedPiece.setLoc(x, y);
                invalidate();
                pieceSelected = false;
                counter++;
                currColor = board.board8Color[x][y];//next piece color
            }
        }
    }

    public void p2Turn(int x, int y){
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (p2[i].getX() == x && p2[i].getY() == y && (currColor == -1 || currColor == p2[i].getColor())) {
                    pieceSelected = true;
                    selectedPiece = p2[i];
                    clickY = p2[i].getY();
                    cliclX = p2[i].getX();
                    invalidate();
                    break;
                }
            }
        }
        else{
            if(movable(x, y) && !blocked(x, y)){
                selectedPiece.setLoc(x, y);
                invalidate();
                pieceSelected = false;
                counter++;
                currColor = board.board8Color[x][y];
            }
        }
    }

    private boolean movable(int x, int y){

        if(counter % 2 == 0 && selectedPiece.getY() > y)
            return false;
        else if(counter % 2 == 1 && selectedPiece.getY() < y)
            return false;

        if(x == selectedPiece.getX() && y == selectedPiece.getY())
            return  false;

        if(selectedPiece.getX() == x){
            return true;
        }
        else if((int)Math.abs(selectedPiece.getX() - x) == (int)Math.abs(selectedPiece.getY() - y)){
            return true;
        }
        return false;
    }//eventually won't need this method, once displayMoves() is finished

    private boolean blocked(int x, int y){

        int sx = x < selectedPiece.getX() ? x : selectedPiece.getX();
        int ex = x > selectedPiece.getX() ? x : selectedPiece.getX();
        int sy = y < selectedPiece.getY() ? y : selectedPiece.getY();
        int ey = y > selectedPiece.getY() ? y : selectedPiece.getY();

        for(int i = sx, j = sy; i <= ex && j <= ey; i++, j++){
            for(int k = 0; k < boardDimension; k++){
                if(i != selectedPiece.getX() && j != selectedPiece.getY())
                    if((p1[k].getX() == i && p1[k].getY() == j) || (p2[k].getX() == i && p2[k].getY() == j)){
                        Log.d("TAG", sx + " " + sy + " " + ex + " " + ey + " " + i + " " + j);
                        return true;
                    }
            }
        }

        if(sx == ex)
            for(int i = sy; i <= ey; i++){
                for(int j = 0; j < boardDimension; j++){
                    if(i != selectedPiece.getY())
                        if((p1[j].getY() == i && p1[j].getX() == sx) || (p2[j].getY() == i && p2[j].getX() == sx)) {
                            Log.d("TAG", " true");
                            return true;
                        }
                }

            }
        return false;
    }//eventually won't need this method, once displayMoves() is finished
}