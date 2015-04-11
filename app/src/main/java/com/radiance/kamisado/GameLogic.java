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
        players = new Player[2];
        board = new Board();
        players[PLAYER_ONE] = new HumanPlayer(PLAYER_ONE);
        switch (VERSUS_TYPE) {
            case MainActivity.TWO_PLAY_PRESSED:
                players[PLAYER_TWO] = new HumanPlayer();
                Log.v("Game", "HumanPlayer");
                break;
            case MainActivity.PLAY_PRESSED:
                players[PLAYER_TWO] = new AIPlayer(EASY);
                Log.v("Game", "AIPlayer");
                break;
        }
        currColor = board.getColor(0, 0);
        findPiece(counter % 2);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);

        gameBoardView.setSelectedPiece(selectedPiece);
        gameBoardView.setAvailMoves(availMoves);
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
        //findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
        gameBoardView.drawBoard(board);
    }

    private void p1Turn(int x, int y){

        //Initiating first move of the game
        if(!pieceSelected) {
            if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_ONE) {
                pieceSelected = true;
                selectedPiece = board.getTile(y,x).getPiece();
                //findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
                gameBoardView.drawBoard(board);
            }
        }
        else{
            //Deselecting on first move\
            if(firstMove){
                if (board.getTile(y, x).getPiece() != null && board.getTile(y, x).getPiece().getOwner() == PLAYER_ONE) {
                    selectedPiece = board.getTile(y,x).getPiece();
                    firstMove = true;
                    // findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
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
                            // findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
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
                                    //  findPossibleMoves(selectedPiece.getX(), selectedPiece.getY());
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



    public int getWin(){
        return win;
    }

    @Override
    public void onTouch(int x, int y){
        Log.v("GAT", "X:" + x + " Y:" + y);

        //first move has not been configured yet
        if (players[counter % 2] instanceof HumanPlayer) {
            Point temp = players[counter % 2].resolveMove(new Point(y, x));
            if (temp != new Point(-1, -1)) {
                if (selectedPiece.getRank() > 0) {

                    counter++;
                } else
                    board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), temp);
                counter++;

                //find next piece
                currColor = board.getColor(y, x);
                findPiece(counter % 2);
                gameBoardView.setAvailMoves(players[counter % 2].calcMoves(board, selectedPiece));


            }
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
