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
public class GameBoardView extends View {
    //gameBoardVariables
    private Paint paint;//make these variables easier to read
    private Board board;
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

    private int[] playerColor = {Color.BLACK, Color.WHITE};

    private int eventAction = -1;
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;

    private boolean firstTime = true;
    //score array
    private int boardDimension = 8;
    /* private Piece[][] pieces = new Piece[2][boardDimension];*/
    private GameLogic gameLogic = new GameLogic(this, boardDimension);
    private GameBoardView.OnBoardEvent onBoardEvent = (GameBoardView.OnBoardEvent) gameLogic;
    private int PLAYER_TWO = 0;
    private int PLAYER_ONE = 1;
    private int EMPTY = -1;
    private int MATCH_TYPE;
    private int VERSUS_TYPE;
    private ArrayList<Point> availMoves = new ArrayList<>();
    private Piece selectedPiece;

    //TODO: Eventually all these constant integers should be switched to enums for typesafety/readability

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();

        //board = new GameLogic(this,BoardDimension);

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

    public void setup(){
        //Only ran once when the view is first created
        if(!firstTime)
            return;

        firstTime = false;

        //Sets up the width and height of the gameLogic on the screen
        //The gameLogic is centered in the screen with a possible border around them
        width = getWidth();
        height = getHeight();

        //Finding the start and end point of the gameLogic with border included
        startX = borderWidth;
        endX = width - borderWidth;

        //The size of each individual square on the gameLogic
        unitSize = (endX - startX) / boardDimension;

        //Finding the start and end point along the vertical axis
        //Calculated by subtracting half the remainder of height with the gameLogic accounted for and then subtracting the gameLogic height for start point
        //End point is same thing except without subtracting the gameLogic width
        startY = height - (height - width) / 2 - width + borderWidth;
        endX = height - (height - width) / 2 + borderWidth;

        //Creates the piece objects

    }//initialisation of the gameboard

    public void drawBoard(Board board) {
        this.board = board;
        invalidate();
    }//Draws the board



    public void setSelectedPiece(Piece p){
        this.selectedPiece = p;
    }

    public void setAvailMoves(ArrayList<Point> list){
        this.availMoves = list;
    }


    private void drawPossibleMoves(Canvas canvas){

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
                onBoardEvent.onSwipeLeft();
            }
            if(initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                onBoardEvent.onSwipeRight();
            }
        }
    }

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup();
        //Draws the board according to color
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                paint.setColor(board.getColor(i, j));
                canvas.drawRect(startX + i * unitSize, startY + j * unitSize, startX + (i + 1) * unitSize, startY + (j + 1) * unitSize, paint);

                if (board.getTile(i, j).getPiece() != null) {
                    Piece temp = board.getTile(i, j).getPiece();
                    paint.setColor(playerColor[temp.getOwner()]);//put in array
                    canvas.drawCircle(startX + j * unitSize + unitSize / 2, startY + unitSize * i + unitSize / 2, unitSize / 2, paint);
                    paint.setColor(temp.getColor());
                    canvas.drawCircle(startX + j * unitSize + unitSize / 2, startY + unitSize * i + unitSize / 2, unitSize / 3, paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawText("" + temp.getRank(), startX + j * unitSize + unitSize / 2, startY + unitSize * i + unitSize / 2, paint);

                }

            }
        }

        //Displays the available moves
        if(selectedPiece != null)
            drawPossibleMoves(canvas);
    }//Draws on the fragment

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (gameLogic.getWin() != -1) {
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