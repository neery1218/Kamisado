package com.radiance.kamisado;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameLogic implements GameBoard.OnBoardEvent {
    int[][] board8Color;
    Board board = new Board();
    private Piece[][] collected;
    private int[] colors = {Color.RED, Color.parseColor("#ED872D"), Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.parseColor("#69359C"), Color.parseColor("#FFB7C5"),
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];
    private GameBoard gameBoard;


    private boolean firstMove = true;
    private boolean pieceSelected = false;

    private int boardDimension = 8;
    private Piece[][] pieces = new Piece[2][boardDimension];

    private int counter = 1;
    private int[] score = new int[2];
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


    private int win = -1;

    public GameLogic(GameBoard gameBoard, int bd) {
        this.boardDimension = bd;
        this.gameBoard = gameBoard;
        board8Color = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        collected = new Piece[2][boardDimension];

        int[][] temp = new int[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                temp[i][j] = board8Color[j][i];

            }
        }
        board8Color = temp;

        for(int i = 0; i < boardDimension; i++){
            pieces[PLAYER_TWO][i] = new Piece(i, 0, board8Color[i][0], 0);
            pieces[PLAYER_ONE][i] = new Piece(i, boardDimension - 1, board8Color[i][boardDimension - 1], 0);
        }

        gameBoard.setPiece(pieces);

	}

    public void search() {//computes for fill left and right

        int counter1 = 0;
        int counter2 = 0;

        for (int i = 0; i < boardDimension; i++)//finds all the pieces starting from the top left to the bottom right
            for (int j = 0; j < boardDimension; j++){
                for (int k = 0; k < pieces[PLAYER_ONE].length; k++){
                    if (pieces[PLAYER_ONE][k].getX() == j && pieces[PLAYER_ONE][k].getY() == i){
                        collected[PLAYER_ONE][counter1] = pieces[PLAYER_ONE][k];
                        counter1++;
                    }
                    if (pieces[PLAYER_TWO][k].getX() == j && pieces[PLAYER_TWO][k].getY() == i){
                        collected[PLAYER_TWO][counter2] = pieces[PLAYER_TWO][k];
                        counter2++;
                    }
                }
            }

    }

    public void fillRight(){

        pieces[PLAYER_ONE] = new Piece[boardDimension];
        pieces[PLAYER_TWO] = new Piece[boardDimension];
        for (int i = 0; i < boardDimension; i++) {
            pieces[PLAYER_ONE][i] = new Piece(i, boardDimension - 1, collected[PLAYER_ONE][boardDimension - 1 - i].getColor(), collected[PLAYER_ONE][boardDimension - 1 - i].getRank());
            pieces[PLAYER_TWO][i] = new Piece(i, 0, collected[PLAYER_TWO][boardDimension - 1 - i].getColor(), collected[PLAYER_TWO][boardDimension - 1 - i].getRank());
        }
        Log.v("fill", "Right");
    }

    public void fillLeft(){

        pieces[PLAYER_ONE] = new Piece[boardDimension];
        pieces[PLAYER_TWO] = new Piece[boardDimension];
        for (int i = 0; i < boardDimension; i++) {
            pieces[PLAYER_ONE][i] = new Piece(i, boardDimension - 1, collected[PLAYER_ONE][i].getColor(), collected[PLAYER_ONE][i].getRank());
            pieces[PLAYER_TWO][i] = new Piece(i, 0, collected[PLAYER_TWO][i].getColor(), collected[PLAYER_TWO][i].getRank());
        }

        Log.v("fill", "Left");
    }

    private void win (){

        win = -1;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            if (pieces[PLAYER_TWO][i].getY() == boardDimension - 1) {
                score[PLAYER_TWO] += Math.max(pieces[PLAYER_TWO][i].getRank(), 1);
                pieces[PLAYER_TWO][i].rankUp();
                win = PLAYER_TWO;
                gameBoard.updateScore(score);
            }
            if (pieces[PLAYER_ONE][i].getY() == 0) {
                score[PLAYER_ONE] += Math.max(pieces[PLAYER_ONE][i].getRank(), 1);
                pieces[PLAYER_ONE][i].rankUp();
                win = PLAYER_ONE;
                gameBoard.updateScore(score);
            }
        }

    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet

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

    public void findPossibleMoves(int x, int y){
        //Array List to store the possible moves
        availMoves = new ArrayList<>();

        //GameLogic array to store position of pieces
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
            Log.d("TAG", availMoves.size() + "");
        }
        else{
            availMoves = searchP1(x, y, availMoves, board);
        }
        gameBoard.setAvailMoves(availMoves);
        gameBoard.setSelectedPiece(selectedPiece);
    }

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

                    int sumoCounter = 0;

                    while (valid(y - i - sumoCounter) && board[x][y - i - sumoCounter] == PLAYER_TWO) {//checks for a chain of opponent pieces
                        if(find(x, y - i - sumoCounter).getRank() >= current.getRank()){
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    Log.v("GAT", "counter:" + sumoCounter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(y - i - sumoCounter) && sumoCounter > 0 && sumoCounter <= current.getRank() && board[x][y - i - sumoCounter] == EMPTY) {
                        sumoPushOption = new Point(x, y - i - sumoCounter);
                        availMoves.add(new Point(x, y - i - sumoCounter));//adds it as a valid move
                        sumoChain = sumoCounter;
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

                    int sumoCounter = 0;

                    while (valid(i + y + sumoCounter) && board[x][i + y + sumoCounter] == PLAYER_ONE) {//checks for a chain of opponent pieces
                        if(find(x, i + y + sumoCounter).getRank() >= current.getRank()){
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    Log.v("GAT", "counter:" + sumoCounter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(i + y + sumoCounter) && sumoCounter > 0 && sumoCounter <= current.getRank() && board[x][y + i + sumoCounter] == EMPTY) {
                        availMoves.add(new Point(x, y + i + sumoCounter));//adds it as a valid move
                        sumoPushOption = new Point(x, y + i + sumoCounter);
                        sumoChain = sumoCounter;
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
        currColor = board8Color[x][sumoPushOption.y];

        for (int j = 0; j < boardDimension; j++) {
            if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                selectedPiece = pieces[PLAYER_ONE][j];
                gameBoard.setPiece(pieces);
                gameBoard.invalidate();
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
        currColor = board8Color[x][sumoPushOption.y];

        //Find the next piece of other player
        for (int j = 0; j < boardDimension; j++) {
            if (pieces[PLAYER_TWO][j].getColor() == currColor) {
                selectedPiece = pieces[PLAYER_TWO][j];
                gameBoard.setPiece(pieces);
                gameBoard.invalidate();
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
                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                    gameBoard.setPiece(pieces);
                    gameBoard.invalidate();
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
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
                        break;
                    }
                }

            }
            //If no moves available then goes to player 1
            if(availMoves.size() == 0){
                counter++;
                Log.d("TAG", counter + " p1 availMoves size 0");
                for(int j = 0; j < boardDimension; j++){
                    if(pieces[PLAYER_TWO][j].getColor() == currColor){
                        selectedPiece = pieces[PLAYER_TWO][j];
                        Log.d("TAG", pieces[PLAYER_TWO][j].toString());
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
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
                        counter++;
                        Log.d("TAG", counter + " p1 normal increment");
                        selectedPiece.setLoc(x, y);
                        currColor = board8Color[x][y];//next piece color
                        for (int j = 0; j < boardDimension; j++) {
                            if (pieces[PLAYER_TWO][j].getColor() == currColor) {
                                selectedPiece = pieces[PLAYER_TWO][j];
                                findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                                gameBoard.setPiece(pieces);
                                gameBoard.invalidate();
                            }
                        }
                    }
                    firstMove = false;


                    win();
                    if(win!= -1) {
                        selectedPiece = null;
                        firstMove = true;
                        pieceSelected = false;
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
                        counter--;
                        search();
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
                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                    gameBoard.setPiece(pieces);
                    gameBoard.invalidate();
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
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
                        break;
                    }
                }

            }

            //If player has no moves available
            if (availMoves.size() == 0) {
                counter++;
                Log.d("TAG", counter + " p2 availMoves size 0");
                for (int j = 0; j < boardDimension; j++) {
                    if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                        selectedPiece = pieces[PLAYER_ONE][j];
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
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
                        counter++;
                        Log.d("TAG", counter + " p2 normal increase");
                        selectedPiece.setLoc(x, y);
                        currColor = board8Color[x][y];//next piece color
                        for (int j = 0; j < boardDimension; j++) {
                            if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                                selectedPiece = pieces[PLAYER_ONE][j];
                                findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                                gameBoard.setPiece(pieces);
                                gameBoard.invalidate();
                            }
                        }

                    }

                    //Increase counter and check if a player has won
                    win();
                    gameBoard.setPiece(pieces);
                    gameBoard.invalidate();
                    if (win != -1) {//if someone has won
                        selectedPiece = null;
                        pieceSelected = false;
                        firstMove = true;
                        gameBoard.setPiece(pieces);
                        gameBoard.invalidate();
                        counter--;
                        search();
                        sumoPushOption = null;
                        return;
                    }

                    break;
                }
            }
        }



    }//Conducting player1's turn

    public int getWin(){
        return win;
    }

    @Override
    public void onTouch(int x, int y){
        if (counter % 2 == PLAYER_TWO) {//determines turn
            //if it's an AI or online player?
            p2Turn(x, y);
        } else {
            p1Turn(x, y);
        }
    }

    @Override
    public void onSwipeLeft(){
        fillLeft();
        win = -1;
        gameBoard.setSelectedPiece(null);
        gameBoard.setPiece(pieces);
        gameBoard.invalidate();

    }

    @Override
    public void onSwipeRight(){
        fillRight();
        win = -1;
        gameBoard.setSelectedPiece(null);
        gameBoard.setPiece(pieces);
        gameBoard.invalidate();
    }

    public class AI {//private or public?

        private int strength;
        private int player;

        public AI(int strength, int player) {
            this.strength = strength;
            this.player = player;
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
