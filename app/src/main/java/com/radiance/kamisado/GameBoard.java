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
    private boolean firstTime = true, firstMove = true, pieceSelected = false;
    private int boardDimension = 8, counter = 1, currColor = -1;
    private Piece[] p1 = new Piece[boardDimension], p2 = new Piece[boardDimension];
    private Board board = new Board(this, boardDimension);
    private Piece selectedPiece;
    private int PLAYER_ONE = 0;
    private int PLAYER_TWO = 1;
    private Point score = new Point(0, 0);
    private ArrayList<Point> availMoves;
    private int eventAction = -1, initialClickX = -1, initialClickY = -1, finalClickX = -1, finalClickY = -1, win = -1;

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }//Calls the super constructor and creates a new paint object

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
        for(int i = 0; i < boardDimension; i++){
            p1[i] = new Piece(i, 0, board.board8Color[i][0], 0);
            p2[i] = new Piece(i, boardDimension - 1, board.board8Color[i][boardDimension - 1], 0);
        }
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
            canvas.drawCircle(startX + p1[i].getX() * unitSize + unitSize / 2, startY + unitSize * p1[i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(p1[i].getColor());
            canvas.drawCircle(startX + p1[i].getX() * unitSize + unitSize / 2, startY + unitSize * p1[i].getY() + unitSize / 2, unitSize / 3, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText("" + p1[i].getRank(), startX + p1[i].getX() * unitSize + unitSize / 2, startY + unitSize * p1[i].getY() + unitSize / 2, paint);

            paint.setColor(Color.WHITE);
            canvas.drawCircle(startX + p2[i].getX() * unitSize + unitSize / 2, startY + unitSize * p2[i].getY() + unitSize / 2, unitSize / 2, paint);
            paint.setColor(p2[i].getColor());
            canvas.drawCircle(startX + p2[i].getX() * unitSize + unitSize / 2, startY + unitSize * p2[i].getY() + unitSize / 2, unitSize / 3, paint);
            paint.setColor(Color.WHITE);
            canvas.drawText("" + p2[i].getRank(), startX + p2[i].getX() * unitSize + unitSize / 2, startY + unitSize * p2[i].getY() + unitSize / 2, paint);
        }
    }//Draws the pieces. Player 1 is on bottom with a black circle around them. Player 2 is on top with white.

    private void win (){

        win = -1;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            if (p1[i].getY() == boardDimension - 1) {
                score.set(score.x + p1[i].getRank(), score.y);
                p1[i].rankUp();
                win = PLAYER_ONE;
            }
            if (p2[i].getY() == 0) {
                score.set(score.x, score.y + p2[i].getRank());
                p2[i].rankUp();
                win =  PLAYER_TWO;
            }
        }

    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet

    private void displayMoves(Canvas canvas, int x, int y){

        //Array List to store the possible moves
        ArrayList<Point> availMoves = new ArrayList<>();

        //Board array to store position of pieces
        int[][] board = new int[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                board[i][j] = 0;
            }
        }

        //Sets the board to 1 if piece is there
        for(int i = 0; i < boardDimension; i++){
            board[p1[i].getX()][p1[i].getY()] = 1;
            board[p2[i].getX()][p2[i].getY()] = 2;
        }

        //Finds available moves for each player
        if(counter % 2 == 0){
            availMoves = searchP1(x,y,availMoves,board);
        }
        else{
            availMoves = searchP2(x,y,availMoves,board);
        }

        //Draws the squares highlighting the available moves
        for(int i = 0; i < availMoves.size(); i++){
            Point p = availMoves.get(i);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(150);
            canvas.drawRect(startX + p.x * unitSize, startY + p.y * unitSize, startX + (p.x + 1) * unitSize, startY + (p.y + 1) * unitSize, paint);
            //switch to circles eventually?
        }
        this.availMoves = availMoves;
    }//Finds available moves of each player
    private boolean valid (int a){
        if (a >= 0 && a < boardDimension)
            return true;
        else
            return false;
    }
    private ArrayList<Point> searchP1(int x, int y, ArrayList<Point> availMoves, int[][] board){
        Piece current = new Piece(0,0,0,0);
        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        //find piece that is making the move
        for (int i = 0; i < p1.length; i++){
            if (p1[i].getX() == x && p2[i].getY() == y)
                current = p1[i];
        }
        for (int i = 1; i <= current.getDistance(); i++) {

            if (!forwardBlocked && valid(i + y)) {//finds moves directly forward
                if (board[x][i + y] == 0)
                    availMoves.add(new Point(x, y + i));
                else
                    forwardBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + y) && valid(i + x)) {
                if (board[x + i][y + i] == 0)
                    availMoves.add(new Point(x + i, y + i));
                else
                    rightDiagonalBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(i + y) && valid(x - i)) {//left diagonal
                if (board[x - i][y + i] == 0)
                    availMoves.add(new Point(x - i, y + i));
                else
                    leftDiagonalBlocked = true;
            }

        }

        return availMoves;
    }//Search for available moves for player 1

    private ArrayList<Point> searchP2(int x, int y, ArrayList<Point> availMoves, int[][] board){

        Piece current = new Piece(0,0,0,0);
        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        //find piece that is making the move
        for (int i = 0; i < p2.length; i++){
            if (p2[i].getX() == x && p2[i].getY() == y)
                current = p2[i];
        }
        Log.v("GAT", "Current Distance:" + current.getDistance() + " Rank:" + current.getRank());
        for (int i = 1; i <= current.getDistance(); i++) {

            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board[x][y - i] == 0)
                    availMoves.add(new Point(x, y - i));
                else
                    forwardBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(y - i) && valid(x - i)) {//left diagonal
                if (board[x - i][y - i] == 0)
                    availMoves.add(new Point(x - i, y - i));
                else
                    leftDiagonalBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + x) && valid(y - i)) {//right diagonal
                if (board[x + i][y - i] == 0)
                    availMoves.add(new Point(x + i, y - i));
                else
                    rightDiagonalBlocked = true;
            }

        }

        return availMoves;
    }//Search for moves for player 2

    private void p1Turn(int x, int y){

        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (p1[i].getX() == x && p1[i].getY() == y) {
                    pieceSelected = true;
                    selectedPiece = p1[i];
                    invalidate();
                    break;
                }
            }
        }
        else {
            if(firstMove){
                for(int i = 0; i < boardDimension; i++){
                    if(p1[i].getX() == x && p1[i].getY() == y){
                        selectedPiece = p1[i];
                        firstMove = true;
                        invalidate();
                        break;
                    }
                }

            }
            //If player has no moves available
            if (availMoves.size() == 0) {
                counter++;
                for (int j = 0; j < boardDimension; j++) {
                    if (p2[j].getColor() == currColor) {
                        selectedPiece = p2[j];
                        invalidate();
                    }
                }
                return;
            }

            //Finds if the clicked square is an available move
            for (int i = 0; i < availMoves.size(); i++) {
                Point temp = availMoves.get(i);
                if (temp.x == x && temp.y == y) {
                    selectedPiece.setLoc(x, y);
                    counter++;
                    win();
                    invalidate();
                    if (win!= -1) {
                        selectedPiece = null;
                        pieceSelected = false;
                        firstMove = true;
                        invalidate();
                        counter--;
                        return;
                    }
                    currColor = board.board8Color[x][y];//next piece color
                    for (int j = 0; j < boardDimension; j++) {
                        if (p2[j].getColor() == currColor) {
                            selectedPiece = p2[j];
                            invalidate();
                        }
                    }
                    break;
                }
            }
        }
    }//Conducting player1's turn

    private void p2Turn(int x, int y){

        //Initiating first move of the game
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
            //Deselecting on first move\
            if(firstMove){
                for(int i = 0; i < boardDimension; i++){
                    if(p2[i].getX() == x && p2[i].getY() == y){
                        selectedPiece = p2[i];
                        firstMove = true;
                        invalidate();
                        break;
                    }
                }

            }
            //If no moves available then goes to player 1
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
            //Finds if the clicked square is an available move
            for(int i = 0; i < availMoves.size(); i++){
                Point temp = availMoves.get(i);
                if(temp.x == x && temp.y == y){
                    firstMove = false;
                    selectedPiece.setLoc(x, y);
                    counter++;
                    win();
                    if(win!= -1) {
                        selectedPiece = null;
                        firstMove = true;
                        pieceSelected = false;
                        invalidate();
                        counter--;
                        return;
                    }
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
    }//Conducting player2's turn

    @Override
    public void onDraw (Canvas canvas){
        super.onDraw(canvas);
        setup(canvas);
        drawBoard(canvas);
        drawPiece(canvas);

        //Displays the available moves
        if(selectedPiece != null)
            displayMoves(canvas, selectedPiece.getX(), selectedPiece.getY());
    }//Draws on the fragment

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int e = event.getAction();

        //If player has won then swiping left or right will reset the board in that direction
        if(win == 1){
            if(event.getAction() == 0){
                initialClickX = (int)event.getX();
                initialClickY = (int)event.getY();
            }
            else if(event.getAction() == 2){
                finalClickX = (int)event.getX();
                finalClickY = (int)event.getY();
            }
            else if(event.getAction() == 1){
                if(finalClickX - initialClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                    initialClickX = -1; finalClickX = -1; initialClickY = -1; finalClickY = -1;
                    //TODO add the reset methods
                    Piece[][] temp = board.fillLeft(p1, p2);
                    p1 = temp[0]; p2 = temp[1];
                    invalidate();
                    win = -1;
                }
                if(initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                    //TODO add the reset methods
                    Piece[][] temp = board.fillRight(p1, p2);
                    p1 = temp[0]; p2 = temp[1];
                    invalidate();
                    win = -1;
                }
            }
        }
        else if(win == 0){
            if(event.getAction() == 0){
                initialClickX = (int)event.getX();
                initialClickY = (int)event.getY();
            }
            else if(event.getAction() == 2){
                finalClickX = (int)event.getX();
                finalClickY = (int)event.getY();
            }
            else if(event.getAction() == 1){;
                if(finalClickX - initialClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                    initialClickX = -1; finalClickX = -1; initialClickY = -1; finalClickY = -1;
                    //TODO add the reset methods
                    Piece[][] temp = board.fillRight(p1, p2);
                    p1 = temp[0]; p2 = temp[1];
                    invalidate();
                    win = -1;
                }
                if(initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                    //TODO add the reset methods
                    Piece[][] temp = board.fillLeft(p1, p2);
                    p1 = temp[0]; p2 = temp[1];
                    invalidate();
                    win = -1;
                }
            }
        }

        //If game was not won and player released screen
        if(e == 1 && win == -1){
            //Finds the x and y location in terms of the board
            float x = event.getX(), y = event.getY();
            int convertedX = (int) ((x - startX) / unitSize), convertedY = (int) ((y - startY) / unitSize);//converts the passed coordinates into a location on the board
            if (counter % 2 == PLAYER_ONE) {//determines turn
                p1Turn(convertedX, convertedY);
            } else {
                p2Turn(convertedX, convertedY);
            }
        }
        return true;
    }//Handles the touch events

    public class AI {

        public AI(){

        }

        public Point move(){
            return availMoves.get((int)(Math.random()) * availMoves.size());
        }

    }
}