package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameControl implements GameBoardView.OnBoardEvent {
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;
    private static boolean firstMove = true;
    Board board = new Board();
    private Point inValid = new Point(-1, -1);
    private Player[] players;
    private GameBoardView gameBoardView;
    private int[] scores = {1, 3, 7, 15};
    private int boardDimension = 8;
    private int counter = 1;
    private int[] score = new int[2];
    private int currColor = -1;
    private Piece selectedPiece;
    private ArrayList<Point> availMoves;
    private int sumoChain = 0;
    //Strength Variables for AI
    private int EASY = 0;
    private int MEDIUM = 1;
    private int HARD = 2;
    private int win = -1;
    private int deadlockCount = 0;

    public GameControl(GameBoardView gameBoardView, int bd, int VERSUS_TYPE) {
        this.boardDimension = bd;
        this.gameBoardView = gameBoardView;
        players = new Player[2];
        board = new Board();
        players[PLAYER_TWO] = new HumanPlayer(PLAYER_TWO);
        switch (VERSUS_TYPE) {
            case MainActivity.TWO_PLAY_PRESSED:
                players[PLAYER_ONE] = new HumanPlayer(PLAYER_ONE);
                Log.v("Game", "HumanPlayer");
                break;
            case MainActivity.PLAY_PRESSED:
                players[PLAYER_ONE] = new AIPlayer(EASY, PLAYER_ONE);
                Log.v("Game", "AIPlayer");
                break;
        }
        currColor = board.getColor(boardDimension - 1, 0);
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);

        selectedPiece = null;
        availMoves = new ArrayList<>();
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

    }



    public void resolveSumoPushP1() {
        //find pieces that are gonna get sumo pushed
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));

        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
    }

    public void resolveSumoPushP2() {

        //Pushing from the other end so it doesn't get overwritten
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX()));


        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX()));

    }

    public boolean resolveFirstMove(int x, int y) {
        if (!board.getTile(y, x).isEmpty()) {
            selectedPiece = board.getTile(y, x).getPiece();
            availMoves = players[counter % 2].calcMoves(board, selectedPiece);
            gameBoardView.setSelectedPiece(selectedPiece);
            gameBoardView.setAvailMoves(availMoves);
            gameBoardView.drawBoard(board);
            return false;
        } else if (selectedPiece == null)
            return false;
        return true;
    }

    public void resolveNormalMove(int x, int y){
        currColor = board.getColor(y, x);
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);
        Log.v("Game", "availMoves: " + availMoves.size());
        if (availMoves.size() == 0 && win == -1) {//if there are no available moves, it skips the player's turn
            deadlockCount++;
            if (deadlockCount == 2) {//this means that both players can't move
                win = counter % 2;
                //TODO: make deadlock screen

            } else {
                counter++;
                resolveNormalMove(selectedPiece.getX(), selectedPiece.getY());
            }
        } else {

            deadlockCount = 0;
        }
        if (win != -1) {
            availMoves = new ArrayList<Point>();
            Log.v("game", "hi");
        }

        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

    }

    public int getWin() {
        return win;
    }

    @Override
    public void onTouch(int x, int y) {

        if (firstMove) {
            if(players[counter % 2] instanceof HumanPlayer && !resolveFirstMove(x, y))
                return;
        }
        firstMove = false;


        Point temp = players[counter % 2].resolveMove(new Point(y, x));
        Log.v("temp", temp.x + " " + temp.y);
        if (!temp.equals(inValid)) {
            if (selectedPiece.getRank() > 0 && temp.equals(players[counter % 2].getSumoPushPoint())) {
                sumoChain = players[counter % 2].getSumoChain();
                switch (counter % 2) {
                    case PLAYER_TWO:
                        resolveSumoPushP1();
                        break;
                    case PLAYER_ONE:
                        resolveSumoPushP2();
                        break;
                }
                counter++;
            } else
                board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), temp);
            counter++;


            //find next piece
            Point winPoint = GameLogic.win(board);
            if(!winPoint.equals(-1,-1)){
                Piece winPiece = board.getTile(winPoint.y, winPoint.x).getPiece();
                int winPlayer = winPiece.getOwner();
                score[winPlayer] += scores[winPiece.getRank()];
                win = winPlayer;
                gameBoardView.updateScore(score);
                board.rankUp(winPiece.getY(), winPiece.getX());
            }
            else{
                win = -1;
            }
            resolveNormalMove(temp.y, temp.x);
            if (win != -1) {
                Log.v("game", "somebody has won");
                counter = win;

                //if it's a player, just return and wait for swipe
                //if ai,

                if (players[counter % 2] instanceof AIPlayer) {
                    onSwipeLeft();
                    Point A = players[counter % 2].selectPiece(board);
                    selectedPiece = board.getTile(A.x, A.y).getPiece();
                    availMoves = players[counter % 2].calcMoves(board, selectedPiece);
                    onTouch(-1, -1);


                } else
                    return;

            } else {


                if (players[counter % 2] instanceof AIPlayer && win == -1)
                    onTouch(-1, -1);
            }


        }
    }

    public void reset() {
        counter = win;
        win = -1;
        firstMove = true;
        selectedPiece = null;
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
