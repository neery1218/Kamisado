package com.radiance.kamisado;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by neerajen on 31/03/15.
 */
public class GameBoardView extends View implements ValueAnimator.AnimatorUpdateListener{
    //gameBoardVariables
    private Paint paint;//make these variables easier to read
    private Board board;
    private TextView scoreView;
    private float startX = -1;
    private float endX = -1;
    private float startY = -1;
    private float endY = -1;
    private float width = -1;
    private float height = -1;
    private float borderWidth = 0;
    private float unitSize = 0;
    private int[] playerColor = {Color.parseColor("#090404"), Color.parseColor("#ffecf0f1")};

    private int eventAction = -1;
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;

    private boolean firstTime = true;
    //score array
    private int boardDimension = 8;
    /* private Piece[][] pieces = new Piece[2][boardDimension];*/
    private GameControl gameControl;
    private GameBoardView.OnBoardEvent onBoardEvent;
    private int PLAYER_ONE = GameControl.PLAYER_ONE;
    private int PLAYER_TWO = GameControl.PLAYER_TWO;
    private int MATCH_TYPE;
    private int VERSUS_TYPE;
    private ArrayList<Point> availMoves = new ArrayList<>();
    private Piece selectedPiece;
    private Piece init, fin;
    private ValueAnimator animator;

    private double[] x = {0, 1, 1, 0, -1, -1};
    private double[] y = {1, 0.7, -0.7, -1, -0.7, 0.7};
    private float outerEdge = 0.9f; //space between outer and inner edge is the player color piece
    private float innerEdge = 0.7f;

    private float rankEdge = 0.3f;
    private float[] rankX = {-0.7071067f, 0, 0.7071067f};
    private float[] rankY = {-0.7071067f, 0.7f, -0.7071067f};


    //TODO: Eventually all these constant integers should be switched to enums for typesafety/readability

    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setTextSize(90);

        //board = new GameControl(this,BoardDimension);

        MATCH_TYPE = GamePlayFragment.getMATCH_TYPE();
        VERSUS_TYPE = GamePlayFragment.getVERSUS_TYPE();
        gameControl = new GameControl(this, boardDimension, VERSUS_TYPE);
        onBoardEvent = gameControl;
        Log.v("Game", "versustype:" + VERSUS_TYPE);
        Log.v("Game", "matchType:" + MATCH_TYPE);
        animator = new ValueAnimator();
        animator.addUpdateListener(this);

    }//Calls the super constructor and creates a new paint object

    public void setScoreView(TextView textView) {
        scoreView = textView;
        updateScore(new int[]{0, 0});

    }

    public void updateScore(int[] score) {
        scoreView.setText(score[PLAYER_TWO] + " " + score[PLAYER_ONE]);
        if (score[PLAYER_TWO] >= MATCH_TYPE || score[PLAYER_ONE] >= MATCH_TYPE) {
            Log.v("Game", "Win");
        }
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


    public void drawBoard(Board board, Piece init, Piece fin, Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
        this.board = board;
        this.init = init;
        this.fin = fin;
        invalidate();
    }//Draws the board

    public void drawBoard(Board board, Piece piece) {
        this.board = board;
        this.selectedPiece = piece;
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
            paint.setColor(board.getColor(availMoves.get(i).x, availMoves.get(i).y));
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(255);
            canvas.drawRect(startX + p.y * unitSize, startY + p.x * unitSize, startX + (p.y + 1) * unitSize, startY + (p.x + 1) * unitSize, paint);
            //switch to circles eventually?
        }

    }

    private void drawPiece(Canvas canvas, int r, int c, int player, int rank) {
        Paint playerPaint = new Paint();
        playerPaint.setColor(playerColor[player]);
        playerPaint.setStyle(Paint.Style.FILL);

        Paint piecePaint = new Paint();
        piecePaint.setColor(board.getTile(r, c).getPiece().getColor());
        piecePaint.setStyle(Paint.Style.FILL);

        Path outerPath = new Path();
        Path innerPath = new Path();
        //find center
        double xCenter = startX + c * unitSize + (unitSize / 2), yCenter = startY + r * unitSize + (unitSize / 2);

        outerPath.reset(); // only needed when reusing this path for a new build
        innerPath.reset();
        double radius = unitSize / 2;
        outerPath.moveTo(Math.round(xCenter + x[0] * outerEdge * radius), Math.round(yCenter + y[0] * outerEdge * radius)); // used for first point
        innerPath.moveTo(Math.round(xCenter + x[0] * innerEdge * radius), Math.round(yCenter + y[0] * innerEdge * radius));
        for (int i = 1; i < x.length; i++) {
            outerPath.lineTo(Math.round(xCenter + x[i] * outerEdge * radius), Math.round(yCenter + y[i] * outerEdge * radius));
            innerPath.lineTo(Math.round(xCenter + x[i] * innerEdge * radius), Math.round(yCenter + y[i] * innerEdge * radius));
        }
        //  playerPaint.setColor(Color.BLACK);
        playerPaint.setAntiAlias(true);
        piecePaint.setAntiAlias(true);
        /*canvas.drawPath(outerPath, playerPaint);
        canvas.drawPath(innerPath, piecePaint);*/
        for (int i = 0; i < rank; i++) {
            canvas.drawCircle((float) xCenter + rankEdge * (float)radius * rankX[i], (float) yCenter + rankEdge * (float)radius* rankY[i], unitSize / 2 * rankEdge, playerPaint);
        }

    }

    public void resolveSwipe(MotionEvent event){
        if(event.getAction() == 0){
            initialClickX = (int)event.getX();
            initialClickY = (int)event.getY();
        }
        else if(event.getAction() == 1){
            if (gameControl.aiWin()) {
                onBoardEvent.onTouch(-1, -1);
            }
            finalClickX = (int)event.getX(); finalClickY = (int)event.getY();
            if(finalClickX - initialClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){

                initialClickX = -1; finalClickX = -1; initialClickY = -1; finalClickY = -1;
                onBoardEvent.onSwipeRight();
            }
            if (initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100) {
                onBoardEvent.onSwipeLeft();
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        setup();
        Log.d("ASDF", "called");
        //Draws the board according to color
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                paint.setColor(board.getColor(i, j));
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);

                if(selectedPiece != null) {
                    paint.setColor(Color.parseColor("#090404"));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setAlpha(150);
                    canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);
                }

                if (!board.getTile(i, j).isEmpty()) {
                    if(selectedPiece != null && i == selectedPiece.getY() && j == selectedPiece.getX()) {
                        Log.d("ASDF", selectedPiece.toString());
                        paint.setColor(board.getColor(i, j));
                        paint.setStyle(Paint.Style.FILL);
                        paint.setAlpha(255);
                        canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);
                    }


                    paint.setAlpha(255);
                    Piece temp = board.getTile(i, j).getPiece();
                    temp.draw(canvas, paint, startX, startY, unitSize, PLAYER_TWO, PLAYER_ONE);
                    drawPiece(canvas, i, j, temp.getOwner(), temp.getRank());

                }
            }
        }

        //Displays the available moves
        if (selectedPiece != null)
            drawPossibleMoves(canvas);
    }//Draws on the fragment

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (!gameControl.getWin().equals(-1, -1)) {
            resolveSwipe(event);
        }
        else if(event.getAction() == 1){
            float x = event.getX(), y = event.getY();
            int convertedX = (int) ((x - startX) / unitSize), convertedY = (int) ((y - startY) / unitSize);//converts the passed coordinates into a location on the board
            if (valid(convertedX) && valid(convertedY))
                onBoardEvent.onTouch(convertedX, convertedY);
        }
        return true;
    }//Handles the touch events

    private boolean valid(int a) {
        return (a >= 0 && a < boardDimension);
    }//Finds available moves of each player

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

    }

    public interface OnBoardEvent{
        void onTouch(int x, int y);

        void onSwipeRight();

        void onSwipeLeft();
    }
}