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

    private Paint paint;//make these variables easier to read

    private float startX = -1;
    private float endX = -1;
    private float startY = -1;
    private float endY = -1;
    private float width = -1;
    private float height = -1;
    private float borderWidth = 0;
    private float unitSize = 0;

    private boolean firstTime = true;
    private boolean firstMove = true;
    private boolean pieceSelected = false;

    private int boardDimension = 8;
    private Piece[][] pieces = new Piece[2][boardDimension];
    private Board board = new Board(this, boardDimension);
    private int counter = 1;
    private int currColor = -1;
    private Piece selectedPiece;

    private int PLAYER_TWO = 0;
    private int PLAYER_ONE = 1;
    private int EMPTY = -1;

    private ArrayList<Point> availMoves;
    private Point sumoPushOption = new Point(0, 0);

    private int sumoChain = 0;

    private int MATCH_TYPE;
    private int VERSUS_TYPE;

    //Strength Variables for AI
    private int EASY = 0;
    private int MEDIUM = 1;
    private int HARD = 2;

    //score array
    private int[] score;

    private int eventAction = -1;
    private int initialClickX = -1;
    private int initialClickY = -1;
    private int finalClickX = -1;
    private int finalClickY = -1;
    private int win = -1;

    //TODO: Eventually all these constant integers should be switched to enums for typesafety/readability

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        score = new int[2];
        score[0] = 0;
        score[1] = 0;

        MATCH_TYPE = GamePlayFragment.getMATCH_TYPE();
        VERSUS_TYPE = GamePlayFragment.getVERSUS_TYPE();
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
            pieces[PLAYER_TWO][i] = new Piece(i, 0, board.board8Color[i][0], 0);
            pieces[PLAYER_ONE][i] = new Piece(i, boardDimension - 1, board.board8Color[i][boardDimension - 1], 0);
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

    private void win (){

        win = -1;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            if (pieces[PLAYER_TWO][i].getY() == boardDimension - 1) {
                score[PLAYER_TWO] += pieces[PLAYER_TWO][i].getRank();
                pieces[PLAYER_TWO][i].rankUp();
                win = PLAYER_TWO;
            }
            if (pieces[PLAYER_ONE][i].getY() == 0) {
                score[PLAYER_ONE] += pieces[PLAYER_ONE][i].getRank();
                pieces[PLAYER_ONE][i].rankUp();
                win = PLAYER_ONE;
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
                board[i][j] = -1;
            }
        }

        //Sets the board to 1 if piece is there
        for(int i = 0; i < boardDimension; i++){
            board[pieces[PLAYER_TWO][i].getX()][pieces[PLAYER_TWO][i].getY()] = PLAYER_TWO;
            board[pieces[PLAYER_ONE][i].getX()][pieces[PLAYER_ONE][i].getY()] = PLAYER_ONE;
        }

        //Finds available moves for each player
        if (counter % 2 == PLAYER_TWO) {
            availMoves = searchP2(x, y, availMoves, board);
        }
        else{
            availMoves = searchP1(x, y, availMoves, board);
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
    }

    private Piece find(int x, int y){
        for(int i = 0; i < boardDimension; i++){
            if(pieces[PLAYER_TWO][i].getX() == x && pieces[PLAYER_TWO][i].getY() == y)
                return pieces[PLAYER_TWO][i];
            else if(pieces[PLAYER_ONE][i].getX() == x && pieces[PLAYER_ONE][i].getY() == y)
                return pieces[PLAYER_ONE][i];
        }
        return null;
    }

    private boolean valid (int a){
        if (a >= 0 && a < boardDimension)
            return true;
        else
            return false;
    }//Finds available moves of each player

    private ArrayList<Point> searchP1(int x, int y, ArrayList<Point> availMoves, int[][] board){

        Piece current = new Piece(0,0,0,0);
        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        //find piece that is making the move
        for (int i = 0; i < pieces[PLAYER_ONE].length; i++){
            if (pieces[PLAYER_ONE][i].getX() == x && pieces[PLAYER_ONE][i].getY() == y)
                current = pieces[PLAYER_ONE][i];
        }
        Log.v("GAT", "Current Distance:" + current.getDistance() + " Rank:" + current.getRank());
        for (int i = 1; i <= current.getDistance(); i++) {

            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board[x][y - i] == EMPTY)
                    availMoves.add(new Point(x, y - i));
                else if (current.getRank() > 0) {//check for sumoPushes

                    int counter = 0;

                    while (valid(y - i - counter) && board[x][y - i - counter] == PLAYER_TWO) {//checks for a chain of opponent pieces
                        if(find(x, y - i - counter).getRank() >= current.getRank()){
                            counter = 0;
                            break;
                        }
                        counter++;
                    }
                    Log.v("GAT", "counter:" + counter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(y - i - counter) && counter > 0 && counter <= current.getRank() && board[x][y - i - counter] == EMPTY) {
                        sumoPushOption = new Point(x, y - i - counter);
                        availMoves.add(new Point(x, y - i - counter));//adds it as a valid move
                        sumoChain = counter;
                    }


                    forwardBlocked = true;
                }
                else
                    forwardBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(y - i) && valid(x - i)) {//left diagonal
                if (board[x - i][y - i] == EMPTY)
                    availMoves.add(new Point(x - i, y - i));
                else
                    leftDiagonalBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + x) && valid(y - i)) {//right diagonal
                if (board[x + i][y - i] == EMPTY)
                    availMoves.add(new Point(x + i, y - i));
                else
                    rightDiagonalBlocked = true;
            }

        }

        return availMoves;
    }//Search for moves for player 2

    private ArrayList<Point> searchP2(int x, int y, ArrayList<Point> availMoves, int[][] board){
        Piece current = new Piece(0,0,0,0);
        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;
        Log.v("GAT", "Current Distance:" + current.getDistance() + " Rank:" + current.getRank());
        //find piece that is making the move
        for (int i = 0; i < pieces[PLAYER_TWO].length; i++){
            if (pieces[PLAYER_TWO][i].getX() == x && pieces[PLAYER_TWO][i].getY() == y)
                current = pieces[PLAYER_TWO][i];
        }
        Log.v("GAT", "Current Distance:" + current.getDistance() + " Rank:" + current.getRank());
        for (int i = 1; i <= current.getDistance(); i++) {//checking for available moves

            //have to look for sumo pushed though
            if (!forwardBlocked && valid(i + y)) {//finds moves directly forward
                if (board[x][i + y] == EMPTY)
                    availMoves.add(new Point(x, y + i));
                else if (current.getRank() > 0) {//check for sumoPushes

                    int counter = 0;

                    while (valid(i + y + counter) && board[x][i + y + counter] == PLAYER_ONE) {//checks for a chain of opponent pieces
                        if(find(x, i + y + counter).getRank() >= current.getRank()){
                            counter = 0;
                            break;
                        }
                        counter++;
                    }
                    Log.v("GAT", "counter:" + counter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(i + y + counter) && counter > 0 && counter <= current.getRank() && board[x][y + i + counter] == EMPTY) {
                        availMoves.add(new Point(x, y + i + counter));//adds it as a valid move
                        sumoPushOption = new Point(x, y + i + counter);
                        sumoChain = counter;
                    }


                    forwardBlocked = true;
                }
                else
                    forwardBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + y) && valid(i + x)) {
                if (board[x + i][y + i] == EMPTY)
                    availMoves.add(new Point(x + i, y + i));
                else
                    rightDiagonalBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(i + y) && valid(x - i)) {//left diagonal
                if (board[x - i][y + i] == EMPTY)
                    availMoves.add(new Point(x - i, y + i));
                else
                    leftDiagonalBlocked = true;
            }

        }

        return availMoves;
    }//Search for available moves for player 1

    public void resolveSumoPushP1(int x){
        //find pieces that are gonna get sumo pushed
        //make points
        for (int j = sumoChain; j >= 1; j--) {
            // findPieceAt (x,y+j);
            for (int k = 0; k < pieces[PLAYER_TWO].length; k++) {
                if (pieces[PLAYER_TWO][k].getX() == x && pieces[PLAYER_TWO][k].getY() == selectedPiece.getY() - j) {
                    pieces[PLAYER_TWO][k].setLoc(x, pieces[PLAYER_TWO][k].getY() - 1);
                }


            }
            counter++;

        }
        selectedPiece.setLoc(selectedPiece.getX(), selectedPiece.getY() - 1);
        currColor = board.board8Color[x][sumoPushOption.y];

        for (int j = 0; j < boardDimension; j++) {
            if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                selectedPiece = pieces[PLAYER_ONE][j];
                invalidate();
            }
        }
    }

    public void resolveSumoPushP2(int x){
        //find pieces that are gonna get sumo pushed
        //make points
        //Pushing from the other end so it doesn't get overwritten
        for (int j = sumoChain; j >= 1; j--) {

            // findPieceAt (x,y+j);
            //Push
            for (int k = 0; k < pieces[PLAYER_ONE].length; k++) {
                if (pieces[PLAYER_ONE][k].getX() == x && pieces[PLAYER_ONE][k].getY() == selectedPiece.getY() + j) {
                    pieces[PLAYER_ONE][k].setLoc(x, pieces[PLAYER_ONE][k].getY() + 1);
                }
            }
            counter++;
        }

        //Set location of piece to 1 square in front of it
        selectedPiece.setLoc(selectedPiece.getX(), selectedPiece.getY() + 1);
        currColor = board.board8Color[x][sumoPushOption.y];

        //Find the next piece of other player
        for (int j = 0; j < boardDimension; j++) {
            if (pieces[PLAYER_TWO][j].getColor() == currColor) {
                selectedPiece = pieces[PLAYER_TWO][j];
                invalidate();
            }
        }
    }

    private void p1Turn(int x, int y){

        //Initiating first move of the game
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (pieces[PLAYER_ONE][i].getX() == x && pieces[PLAYER_ONE][i].getY() == y) {
                    pieceSelected = true;
                    selectedPiece = pieces[PLAYER_ONE][i];
                    invalidate();
                    break;
                }
            }
        }
        else{
            //Deselecting on first move\
            if(firstMove){
                for(int i = 0; i < boardDimension; i++){
                    if(pieces[PLAYER_ONE][i].getX() == x && pieces[PLAYER_ONE][i].getY() == y){
                        selectedPiece = pieces[PLAYER_ONE][i];
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
                    if(pieces[PLAYER_TWO][j].getColor() == currColor){
                        selectedPiece = pieces[PLAYER_TWO][j];
                        Log.d("TAG", pieces[PLAYER_TWO][j].toString());
                        invalidate();
                    }
                }
            }
            //Finds if the clicked square is an available move
            for(int i = 0; i < availMoves.size(); i++){
                Point temp = availMoves.get(i);
                if(temp.x == x && temp.y == y){
                    if (selectedPiece.getRank() > 0 && sumoPushOption != null && temp.x == sumoPushOption.x && temp.y == sumoPushOption.y) {
                        resolveSumoPushP1(x);

                    } else {
                        selectedPiece.setLoc(x, y);
                        currColor = board.board8Color[x][y];//next piece color
                        for (int j = 0; j < boardDimension; j++) {
                            if (pieces[PLAYER_TWO][j].getColor() == currColor) {
                                selectedPiece = pieces[PLAYER_TWO][j];
                                invalidate();
                            }
                        }
                    }
                    firstMove = false;

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

                    break;
                }
            }
        }
    }//Conducting player2's turn

    private void p2Turn(int x, int y){

        //First move of the game
        if(!pieceSelected) {
            for (int i = 0; i < boardDimension; i++) {
                if (pieces[PLAYER_TWO][i].getX() == x && pieces[PLAYER_TWO][i].getY() == y) {
                    pieceSelected = true;
                    selectedPiece = pieces[PLAYER_TWO][i];
                    invalidate();
                    break;
                }
            }
        }
        //When a piece is already selected
        else {

            //For deselecting a piece
            if(firstMove){
                for(int i = 0; i < boardDimension; i++){
                    if(pieces[PLAYER_TWO][i].getX() == x && pieces[PLAYER_TWO][i].getY() == y){
                        selectedPiece = pieces[PLAYER_TWO][i];
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
                    if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                        selectedPiece = pieces[PLAYER_ONE][j];
                        invalidate();
                    }
                }
                return;
            }

            //Finds if the clicked square is an available move
            for (int i = 0; i < availMoves.size(); i++) {
                Point temp = availMoves.get(i);

                if (temp.x == x && temp.y == y) {

                    //Sumo push
                    if (selectedPiece.getRank() > 0 && sumoPushOption != null && temp.x == sumoPushOption.x && temp.y == sumoPushOption.y) {
                        resolveSumoPushP2(x);
                    }
                    //If sumo push is not committed
                    else {

                        //Sets piece to mouse clicked location
                        selectedPiece.setLoc(x, y);
                        currColor = board.board8Color[x][y];//next piece color
                        for (int j = 0; j < boardDimension; j++) {
                            if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                                selectedPiece = pieces[PLAYER_ONE][j];
                                invalidate();
                            }
                        }

                    }

                    //Increase counter and check if a player has won
                    counter++;
                    win();
                    invalidate();
                    if (win != -1) {//if someone has won
                        selectedPiece = null;
                        pieceSelected = false;
                        firstMove = true;
                        invalidate();
                        counter--;
                        sumoPushOption = null;
                        return;
                    }

                    break;
                }
            }
        }

    }//Conducting player1's turn

    public void resolveSwipe(MotionEvent event){
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
                Piece[][] temp = board.fillLeft(pieces[PLAYER_TWO], pieces[PLAYER_ONE]);
                pieces[PLAYER_TWO] = temp[0]; pieces[PLAYER_ONE] = temp[1];
                invalidate();
                win = -1;
            }
            if(initialClickX - finalClickX > 200 && Math.abs(finalClickY - initialClickY) < 100){
                //TODO add the reset methods
                Piece[][] temp = board.fillRight(pieces[PLAYER_TWO], pieces[PLAYER_ONE]);
                pieces[PLAYER_TWO] = temp[0]; pieces[PLAYER_ONE] = temp[1];
                invalidate();
                win = -1;
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
            displayMoves(canvas, selectedPiece.getX(), selectedPiece.getY());
    }//Draws on the fragment

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int e = event.getAction();

        //If player has won then swiping left or right will reset the board in that direction
        if(win == 1 || win == 0){
            resolveSwipe(event);
        }

        //If game was not won and player released screen
        if(e == 1 && win == -1){
            //Finds the x and y location in terms of the board
            float x = event.getX(), y = event.getY();
            int convertedX = (int) ((x - startX) / unitSize), convertedY = (int) ((y - startY) / unitSize);//converts the passed coordinates into a location on the board
            if (counter % 2 == PLAYER_TWO) {//determines turn
                p2Turn(convertedX, convertedY);
            } else {
                p1Turn(convertedX, convertedY);
            }
        }
        return true;
    }//Handles the touch events

    public class AI {//private or public?

        private int strength;

        public AI(int strength) {
            this.strength = strength;
        }

        public AI() {

        }

        public int getStrength() {
            return strength;
        }

        public void setStrength(int strength) {
            this.strength = strength;
        }

        public Point move(){
            if (strength == EASY)
                return availMoves.get((int) (Math.random()) * availMoves.size());

            return availMoves.get(0);
        }


    }
}