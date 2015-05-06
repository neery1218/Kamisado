package com.radiance.kamisado;

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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by neerajen on 31/03/15.
 */
public class GameBoardView extends View {
    private final Handler animationHandler = new Handler();
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
    private int[] playerColor = {Color.parseColor("#ff34495e"), Color.parseColor("#ffecf0f1")};

    private int eventAction = -1;
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;

    private int NUM_FRAMES = 255;
    private int frameCounter = 0;
    private boolean firstTime = true;
    //score array
    private int boardDimension = 8;
    /* private Piece[][] pieces = new Piece[2][boardDimension];*/
    private GameControl gameControl;
    private GameBoardView.OnBoardEvent onBoardEvent;
    private int PLAYER_ONE = gameControl.PLAYER_ONE;
    private int PLAYER_TWO = gameControl.PLAYER_TWO;
    private int EMPTY = -1;
    private int MATCH_TYPE;
    private int VERSUS_TYPE;
    private ArrayList<Point> availMoves = new ArrayList<>();
    private Piece selectedPiece;
    private Point init, fin;
    private boolean animateMove = false;

    //hexagon hard-coded coordinates
    private double[] x = {0, 1, 1, 0, -1, -1};
    private double[] y = {1, 0.7, -0.7, -1, -0.7, 0.7};
    private double outerEdge = 0.9; //space between outer and inner edge is the player color piece
    private double innerEdge = 0.7;



    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setTextSize(90);

        //board = new GameControl(this,BoardDimension);

        MATCH_TYPE = GamePlayFragment.getMATCH_TYPE();
        VERSUS_TYPE = GamePlayFragment.getVERSUS_TYPE();
        gameControl = new GameControl(this, boardDimension, VERSUS_TYPE);
        onBoardEvent = (GameBoardView.OnBoardEvent) gameControl;
        Log.v("Game", "versustype:" + VERSUS_TYPE);
        Log.v("Game", "matchType:" + MATCH_TYPE);

    }//Calls the super constructor and creates a new paint object

    //TODO: Eventually all these constant integers should be switched to enums for typesafety/readability
    private void drawPiece(Canvas canvas, int r, int c, int player) {
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
        canvas.drawPath(outerPath, playerPaint);
        canvas.drawPath(innerPath, piecePaint);


    }

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


    public void drawBoard(Board board, Point init, Point fin) {
        this.board = board;
        this.init = init;
        this.fin = fin;
        animateMove = true;
        new Thread(new AnimateMove()).start();
        // invalidate();
        Log.d("Animate", "called");
        //TODO Animate
    }//Draws the board

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
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                paint.setColor(Color.parseColor("#090404"));
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(150);
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);
            }
        }

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
        setup();
        //Draws the board according to color
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                paint.setColor(board.getColor(i, j));
                canvas.drawRect(startX + j * unitSize, startY + i * unitSize, startX + (j + 1) * unitSize, startY + (i + 1) * unitSize, paint);

                if (!board.getTile(i, j).isEmpty()) {
                    Piece temp = board.getTile(i, j).getPiece();
                    drawPiece(canvas, i, j, temp.getOwner());
                   /* paint.setColor(playerColor[temp.getOwner()]);
                    canvas.drawText("" + temp.getRank(), startX + j * unitSize + unitSize / 2 - 25, startY + unitSize * i + unitSize / 2 + 30, paint);
*/
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

    public interface OnBoardEvent{
        public void onTouch(int x, int y);

        public void onSwipeRight();

        public void onSwipeLeft();
    }

    private class AnimateMove implements Runnable {

        public AnimateMove() {

        }

        @Override
        public void run() {
            for (frameCounter = 0; frameCounter < NUM_FRAMES; frameCounter++) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //fade in fade out animation via paint.setAlpha() method
                        //original point piece has to be faded out
                        //new piece has to be faded in
                        invalidate();
                    }
                }, 50);
            }

        }
    }
}