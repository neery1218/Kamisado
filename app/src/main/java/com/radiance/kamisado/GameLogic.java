package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameLogic implements GameBoardView.OnBoardEvent{
    private static boolean firstMove = true;
    private final int HUMAN_PLAYER = 0;
    private final int AI_PLAYER = 1;
    private final int ONLINE_PLAYER = 2;
    Board board = new Board();
    private Point inValid = new Point(-1, -1);
    private Player[] players;
    private GameBoardView gameBoardView;
    private int[] scores = {1, 3, 7, 15};
    private boolean pieceSelected = false;
    private int boardDimension = 8;
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

    public GameLogic(GameBoardView gameBoardView, int bd, int VERSUS_TYPE) {
        this.boardDimension = bd;
        this.gameBoardView = gameBoardView;

        board = new Board();
        players[PLAYER_ONE] = new HumanPlayer();
        switch (VERSUS_TYPE) {
            case HUMAN_PLAYER:
                players[PLAYER_TWO] = new HumanPlayer();
                break;
            case AI_PLAYER:
                players[PLAYER_TWO] = new AIPlayer();
                break;
        }

        gameBoardView.drawBoard(board); 

	}

    public boolean isFirstMove() {
        return firstMove;
    }
    private void win (){

        win = -1;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {

            //check if player one has won
            Piece temp = board.getTile(0, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_ONE) {

                score[PLAYER_ONE] += scores[temp.getRank()];
                board.rankUp(0, i);
                win = PLAYER_ONE;
                gameBoardView.updateScore(score);
            }

            temp = board.getTile(boardDimension - 1, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_TWO) {
                score[PLAYER_TWO] += scores[temp.getRank()];
                board.rankUp(boardDimension - 1, i);
                win = PLAYER_TWO;
                gameBoardView.updateScore(score);
            }


        }
    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet


    private boolean valid (int a){
        return (a >= 0 && a < boardDimension);
    }//Finds available moves of each player

    public void findPossibleMoves(int x, int y){
        //Array List to store the possible moves
        availMoves = new ArrayList<>();



        //Finds available moves for each player
        if (counter % 2 == PLAYER_TWO) {
            searchP2();
        }
        else{
            searchP1();
        }
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.setSelectedPiece(selectedPiece);
    }

    private void searchP1() {

        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        int x = selectedPiece.getX();
        int y = selectedPiece.getY();

        Log.v("GAT", "Current Distance:" + selectedPiece.getDistance() + " Rank:" + selectedPiece.getRank());

        for (int i = 1; i <= selectedPiece.getDistance(); i++) {
            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board.getTile(y - i, x).isEmpty()) {
                    availMoves.add(new Point(y - i, x));
                }

                else if (selectedPiece.getRank() > 0) {//check for sumoPushes

                    int sumoCounter = 0;

                    while (valid(y - i - sumoCounter) && !board.getTile(y - i - sumoCounter, x).isEmpty() && board.getTile(y - i - sumoCounter, x).getPiece().getOwner() == PLAYER_TWO) {//checks for a chain of opponent pieces
                        if (board.getTile(y - i - sumoCounter, x).getPiece().getRank() >= selectedPiece.getRank()) {
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    Log.v("GAT", "counter:" + sumoCounter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(y - i - sumoCounter) && sumoCounter > 0 && sumoCounter <= selectedPiece.getRank() && board.getTile(y - i - sumoCounter, x).getPiece() == null) {
                        sumoPushOption = new Point(y - i - sumoCounter, x);
                        availMoves.add(sumoPushOption);//adds it as a valid move
                        sumoChain = sumoCounter;
                    }


                    forwardBlocked = true;
                }
                else
                    forwardBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(y - i) && valid(x - i)) {//left diagonal
                if (board.getTile(y - i, x - i).isEmpty())
                    availMoves.add(new Point(y - i, x - i));
                else
                    leftDiagonalBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + x) && valid(y - i)) {//right diagonal
                if (board.getTile(y - i, i + x).isEmpty())
                    availMoves.add(new Point(y - i, x + i));
                else
                    rightDiagonalBlocked = true;
            }

        }
    }//Search for moves for player 2

    private void searchP2() {
        int x = selectedPiece.getX(), y = selectedPiece.getY();

        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;
        Log.v("GAT", "Current Distance:" + selectedPiece.getDistance() + " Rank:" + selectedPiece.getRank());

        for (int i = 1; i <= selectedPiece.getDistance(); i++) {//checking for available moves

            //have to look for sumo pushed though
            if (!forwardBlocked && valid(i + y)) {//finds moves directly forward
                if (board.getTile(i + y, x).getPiece() == null)
                    availMoves.add(new Point(y + i, x));
                else if (selectedPiece.getRank() > 0) {//check for sumoPushes

                    int sumoCounter = 0;

                    while (valid(i + y + sumoCounter) && board.getTile(i + y + sumoCounter, x).getPiece().getOwner() == PLAYER_ONE) {//checks for a chain of opponent pieces
                        if (board.getTile(i + y + sumoCounter, x).getPiece().getRank() >= selectedPiece.getRank()) {
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    Log.v("GAT", "counter:" + sumoCounter);
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(i + y + sumoCounter) && sumoCounter > 0 && sumoCounter <= selectedPiece.getRank() && board.getTile(y + i + sumoCounter, x).getPiece() == null) {
                        sumoPushOption = new Point(y + i + sumoCounter, x);
                        availMoves.add(sumoPushOption);//adds it as a valid move
                        sumoChain = sumoCounter;
                    }


                    forwardBlocked = true;
                }
                else
                    forwardBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + y) && valid(i + x)) {
                if (board.getTile(y + i, x + i).getPiece() == null)
                    availMoves.add(new Point(y + i, x + i));
                else
                    rightDiagonalBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(i + y) && valid(x - i)) {//left diagonal
                if (board.getTile(y + i, x - i).getPiece() == null)
                    availMoves.add(new Point(y + i, x - i));
                else
                    leftDiagonalBlocked = true;
            }

        }

    }//Search for available moves for player 1

    public void resolveSumoPushP1(int x){
        //find pieces that are gonna get sumo pushed
        //make points
        for (int j = sumoChain; j >= 1; j--) {
            // findPieceAt (x,y+j);
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));


        }
        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
        // counter++;
        currColor = board.getColor(sumoPushOption.x, sumoPushOption.y);
        Log.v("currColor", "" + currColor);
        findPiece(PLAYER_ONE);
        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
        gameBoardView.drawBoard(board);
    }

    private void findPiece(int PLAYER) {
        for (int i = 0; i < board.getHeight(); i++)
            for (int j = 0; j < board.getWidth(); j++) {
                Piece temp = board.getTile(i, j).getPiece();
                if (temp != null && temp.getColor() == currColor && temp.getOwner() == PLAYER) {
                    selectedPiece = temp;
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
            board.move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX()));


        }
        //  counter++;

        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX()));
        currColor = board.getColor(sumoPushOption.y, sumoPushOption.x);
        findPiece(PLAYER_TWO);
        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
        gameBoardView.drawBoard(board);
    }

    private void p1Turn(int x, int y){

        //Initiating first move of the game
        if(!pieceSelected) {
            if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_ONE) {
                pieceSelected = true;
                selectedPiece = board.getTile(y,x).getPiece();
                findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                gameBoardView.drawBoard(board);
            }
        }
        else{
            //Deselecting on first move\
            if(firstMove){
                if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_ONE) {
                    selectedPiece = board.getTile(y,x).getPiece();
                    firstMove = true;
                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                    gameBoardView.drawBoard(board);
                }

            }
            //If no moves available then goes to player 1
            if(availMoves.size() == 0){
                counter++;
                for(int i = 0; i < boardDimension; i++){
                    for(int j = 0; j < boardDimension; j++){
                        if(board.getTile(i, j).getColor() == currColor && board.getTile(i,j).getPiece().getOwner() == PLAYER_TWO){
                            selectedPiece = board.getTile(i,j).getPiece();
                            findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                            gameBoardView.drawBoard(board);
                        }
                    }
                }
            }
            //Finds if the clicked square is an available move
            for(int i = 0; i < availMoves.size(); i++){
                Point temp = availMoves.get(i);
                if (temp.y == x && temp.x == y) {
                    if (selectedPiece.getRank() > 0 && sumoPushOption != null && temp.x == sumoPushOption.x && temp.y == sumoPushOption.y) {
                        resolveSumoPushP1(x);

                    } else {
                        counter++;
                        /*selectedPiece.setLoc(x, y);
                        currColor = board8Color[x][y];//next piece color*/
                        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(y, x));
                        currColor = board.getColor(y, x);

                        for(int j = 0; j < boardDimension; j++){
                            for(int k = 0; k < boardDimension; k++){
                                if (board.getTile(j, k).getPiece() != null && board.getTile(j, k).getPiece().getOwner() == PLAYER_TWO && board.getTile(j, k).getPiece().getColor() == currColor) {
                                    selectedPiece = board.getTile(j,k).getPiece();
                                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                                    gameBoardView.drawBoard(board);
                                }
                            }
                        }
                    }
                    firstMove = false;


                    win();
                    if(win!= -1) {
                        selectedPiece = null;
                        firstMove = true;
                        pieceSelected = false;
                        gameBoardView.drawBoard(board);
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
            if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_TWO) {
                selectedPiece = board.getTile(y,x).getPiece();
                firstMove = true;
                findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                gameBoardView.drawBoard(board);
            }
        }
        //When a piece is already selected
        else {

            //For deselecting a piece
            if(firstMove){
                /*for(int i = 0; i < boardDimension; i++){
                    if(pieces[PLAYER_TWO][i].getX() == x && pieces[PLAYER_TWO][i].getY() == y){
                        selectedPiece = pieces[PLAYER_TWO][i];
                        firstMove = true;
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoardView.drawBoard(board);
                        break;
                    }
                }*/
                if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_TWO) {
                    selectedPiece = board.getTile(y,x).getPiece();
                    firstMove = true;
                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                    gameBoardView.drawBoard(board);
                }
            }

            //If player has no moves available
            if (availMoves.size() == 0) {
                counter++;
                /*for (int j = 0; j < boardDimension; j++) {
                    if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                        selectedPiece = pieces[PLAYER_ONE][j];
                        findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                        gameBoardView.drawBoard(board);
                    }
                }*/
                for(int i = 0; i < boardDimension; i++){
                    for(int j = 0; j < boardDimension; j++){
                        if(board.getTile(i, j).getColor() == currColor && board.getTile(i,j).getPiece().getOwner() == PLAYER_ONE){
                            selectedPiece = board.getTile(i,j).getPiece();
                            findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                            gameBoardView.drawBoard(board);
                        }
                    }
                }
                return;
            }

            //Finds if the clicked square is an available move
            for (int i = 0; i < availMoves.size(); i++) {
                Point temp = availMoves.get(i);

                if (temp.x == y && temp.y == x) {

                    //Sumo push
                    if (selectedPiece.getRank() > 0 && sumoPushOption != null && temp.x == sumoPushOption.x && temp.y == sumoPushOption.y) {
                        resolveSumoPushP2(x);
                    }
                    //If sumo push is not committed
                    else {

                        //Sets piece to mouse clicked location
                        counter++;
                        /*selectedPiece.setLoc(x, y);
                        currColor = board8Color[x][y];//next piece color*/
                        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(y, x));
                        currColor = board.getColor(y, x);
                        /*for (int j = 0; j < boardDimension; j++) {
                            if (pieces[PLAYER_ONE][j].getColor() == currColor) {
                                selectedPiece = pieces[PLAYER_ONE][j];
                                findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                                gameBoardView.drawBoard(board);
                            }
                        }*/
                        for(int j = 0; j < boardDimension; j++){
                            for(int k = 0; k < boardDimension; k++){
                                if (board.getTile(j, k).getPiece() != null && board.getTile(j, k).getPiece().getOwner() == PLAYER_ONE && board.getTile(j, k).getPiece().getColor() == currColor) {
                                    selectedPiece = board.getTile(j,k).getPiece();
                                    findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                                    gameBoardView.drawBoard(board);
                                }
                            }
                        }

                    }

                    //Increase counter and check if a player has won
                    win();
                    gameBoardView.drawBoard(board);
                    if (win != -1) {//if someone has won
                        selectedPiece = null;
                        pieceSelected = false;
                        firstMove = true;
                        gameBoardView.drawBoard(board);
                        counter--;
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
        Log.v("GAT", "X:" + x + " Y:" + y);
        //if is firstMove
        //gameBoardView.setAvailMoves(players[counter%2].calcMoves(x,y));
        //
        //else
        //Point temp = players[counter%2].resolveMoves(x,y);
        //if temp!= (-1,-1)
        //board.move(selectedPiece.getY(),selectedPiece.getX(), temp);
        //gameBoardView.drawBoard(board);
        //counter++;

        if (counter % 2 == PLAYER_TWO) {//determines turn
            //if it's an AI or online player?
            p2Turn(x, y);

        } else {
            p1Turn(x, y);
        }
    }

    public void reset(){
        win = -1;
        gameBoardView.setSelectedPiece(null);
        gameBoardView.drawBoard(board);
    }

    @Override
    public void onSwipeRight() {
        board.search();
        board.fillRight();
        reset();
    }

    @Override
    public void onSwipeLeft() {
        board.search();
        board.fillLeft();
        reset();

    }
}
