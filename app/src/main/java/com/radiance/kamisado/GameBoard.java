package com.radiance.kamisado;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by neerajen on 31/03/15.
 */
public class GameBoard extends View {
    //gameBoardVariables
    private Paint paint;//make these variables easier to read

    private TextView scoreView;
    //testing commit on alternate branch
    //forgot to make a good commit message,
    private float startX = -1;
    private float endX = -1;
    private float startY = -1;
    private float endY = -1;
    private float width = -1;
    private float height = -1;
    private float borderWidth = 0;
    private float unitSize = 0;

    private int eventAction = -1;
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;

    private boolean firstTime = true;
    //score array
    private int boardDimension = 8;
    private Piece[][] pieces = new Piece[2][boardDimension];
    private Board board = new Board(this, boardDimension);
    private GameBoard.OnBoardEvent onBoardEvent = (GameBoard.OnBoardEvent) board;
    private int PLAYER_TWO = 0;
    private int PLAYER_ONE = 1;
    private int EMPTY = -1;
    private int MATCH_TYPE;
    private int VERSUS_TYPE;
    private ArrayList<Point> availMoves = new ArrayList<>();
    private Piece selectedPiece;

    //TODO: Eventually all these constant integers should be switched to enums for typesafety/readability

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();

        //board = new Board(this,BoardDimension);

        MATCH_TYPE = GamePlayFragment.getMATCH_TYPE();
        VERSUS_TYPE = GamePlayFragment.getVERSUS_TYPE();

    }//Calls the super constructor and creates a new paint object

    public void setScoreView(TextView textView) {
        scoreView = textView;
        updateScore(new int[]{0, 0});
    }

    public void updateScore(int[] score) {
        scoreView.setText(score[PLAYER_ONE] + " " + score[PLAYER_TWO]);
    }

    public void setup(Canvas canvas){
        //Only ran once when the view is first created
        if(!firstTime)
            return;

        firstTime = false;

        //Sets up the width and height of the board on the screen
        //The board is centered in the screen with a possible border around them
        width = getWidth();
        height = getHeight();

        //Finding the start and end point of the board with border included
        startX = borderWidth;
        endX = width - borderWidth;

        //The size of each individual square on the board
        unitSize = (endX - startX) / boardDimension;

        //Finding the start and end point along the vertical axis
        //Calculated by subtracting half the remainder of height with the board accounted for and then subtracting the board height for start point
        //End point is same thing except without subtracting the board width
        startY = height - (height - width) / 2 - width + borderWidth;
        endX = height - (height - width) / 2 + borderWidth;

        //Creates the piece objects

    }//initialisation of the gameboard

    public void drawBoard(Canvas c){
        //Draws the board according to color
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                paint.setColor(board.board8Color[i][j]);
                c.drawRect(startX + i * unitSize, startY + j * unitSize, startX + (i + 1) * unitSize, startY + (j + 1) * unitSize, paint);
            }
        }
    }//Draws the board

    public void drawPiece(Canvas canvas){
        paint.setTextSize(30);
        for(int i = 0; i < boardDimension; i++){
            paint.setColor(Color.BLACK);
            canvas.drawCircle(startX + pieces[PLAYER_TWO][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_TWO][i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(pieces[PLAYER_TWO][i].getColor());
            canvas.drawCircle(startX + pieces[PLAYER_TWO][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_TWO][i].getY() + unitSize / 2, unitSize / 3, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("" + pieces[PLAYER_TWO][i].getRank(), startX + pieces[PLAYER_TWO][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_TWO][i].getY() + unitSize / 2, paint);

            paint.setColor(Color.WHITE);
            canvas.drawCircle(startX + pieces[PLAYER_ONE][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_ONE][i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(pieces[PLAYER_ONE][i].getColor());
            canvas.drawCircle(startX + pieces[PLAYER_ONE][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_ONE][i].getY() + unitSize / 2, unitSize / 3, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("" + pieces[PLAYER_ONE][i].getRank(), startX + pieces[PLAYER_ONE][i].getX() * unitSize + unitSize / 2, startY + unitSize * pieces[PLAYER_ONE][i].getY() + unitSize / 2, paint);
        }
    }//Draws the pieces. Player 1 is on bottom with a black circle around them. Player 2 is on top with white.



    public void setSelectedPiece(Piece p){
        this.selectedPiece = p;
    }

    public void setAvailMoves(ArrayList<Point> list){
        this.availMoves = list;
    }

    public void setPiece(Piece[][] p){
        this.pieces = p;
    }

    private void drawPossibleMoves(Canvas canvas, int x, int y){

        //Draws the squares highlighting the available moves
        for(int i = 0; i < availMoves.size(); i++){
            Point p = availMoves.get(i);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(150);
            canvas.drawRect(startX + p.x * unitSize, startY + p.y * unitSize, startX + (p.x + 1) * unitSize, startY + (p.y + 1) * unitSize, paint);
            //switch to circles eventually?
        }
    }

    public void resolveSwipe(MotionEvent event){
        if(event.getAction() == 0){
            initialClickX = (int)event.getX();
            initialClickY = (int)event.getY();
        }
        else if(event.getAction() == 1){
            finalClickX = (int)event.getX(); finalClickY = (int)event.getY();
            if(finalClickX - initialClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                initialClickX = -1; finalClickX = -1; initialClickY = -1; finalClickY = -1;
                /*//TODO add the reset methods
                Piece[][] temp = board.fillLeft(pieces[PLAYER_TWO], pieces[PLAYER_ONE]);
                pieces[PLAYER_TWO] = temp[0]; pieces[PLAYER_ONE] = temp[1];
                invalidate();
                win = -1;*/
                onBoardEvent.onSwipeLeft();
            }
            if(initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                //TODO add the reset methods
                /*Piece[][] temp = board.fillRight(pieces[PLAYER_TWO], pieces[PLAYER_ONE]);
                pieces[PLAYER_TWO] = temp[0]; pieces[PLAYER_ONE] = temp[1];
                invalidate();
                board.getWin() = -1;*/
                onBoardEvent.onSwipeRight();
            }
        }
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);
        drawPiece(canvas);

        //Displays the available moves
        if(selectedPiece != null)
            drawPossibleMoves(canvas, selectedPiece.getX(), selectedPiece.getY());
    }//Draws on the fragment

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(board.getWin() != -1) {
            resolveSwipe(event);
        }
        else if(event.getAction() == 1){
            float x = event.getX(), y = event.getY();
            int convertedX = (int) ((x - startX) / unitSize), convertedY = (int) ((y - startY) / unitSize);//converts the passed coordinates into a location on the board
            onBoardEvent.onTouch(convertedX, convertedY);
        }
        return true;
    }//Handles the touch events



    public interface OnBoardEvent{
        public void onTouch(int x, int y);

        public void onSwipeLeft();

        public void onSwipeRight();
    }
}