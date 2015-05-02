package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameControl implements GameBoardView.OnBoardEvent {//runs the game counter and controls gameBoardView calls
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

    private Point win = new Point(-1, -1);
    private int deadlockCount = 0;
    private boolean aiWin = false;

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
                players[PLAYER_ONE] = new AIPlayer(GamePlayFragment.getAiDifficulty(), PLAYER_ONE);
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


    public void resolveSumoPushP1() {//moves the pieces from a player one sumopush
        //find pieces that are gonna get sumo pushed
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));

        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
    }

    public void resolveSumoPushP2() {//moves the pieces from a player two sumopush

        //Pushing from the other end so it doesn't get overwritten
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX()));


        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX()));

    }

    public boolean resolveFirstMove(int x, int y) {//used to display moves when it's the first move of a game
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

    public void resolveNormalMove(int x, int y) {//finds the next piece and availMoves. also checks for no moves and/or deadlock
        currColor = board.getColor(y, x);
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);
        Log.v("Game", "availMoves: " + availMoves.size());
        if (availMoves.size() == 0 && win.equals(-1, -1)) {//if there are no available moves, it skips the player's turn
            deadlockCount++;
            if (deadlockCount == 2) {//this means that both players can't move
                //win = counter % 2;
                //new rules: if deadlock, it's a tie
                //TODO: make deadlock screen

            } else {
                counter++;
                resolveNormalMove(selectedPiece.getX(), selectedPiece.getY());
            }
        } else {

            deadlockCount = 0;
        }
        if (!win.equals(-1, -1)) {
            availMoves = new ArrayList<Point>();
        }

        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

    }

    public Point getWin() {
        return win;
    }//getter method used by gameBoardView

    public boolean aiWin() {
        return aiWin;
    }

    @Override
    public void onTouch(int x, int y) {//overriden method from the interface: all method calls originate from here

        if (aiWin) {
            aiWin = false;
            Log.v("AITEST", "reset");
            onSwipeLeft();
            Point A = players[counter % 2].selectPiece(board);
            selectedPiece = board.getTile(A.x, A.y).getPiece();
            gameBoardView.setSelectedPiece(selectedPiece);
            availMoves = players[counter % 2].calcMoves(board, selectedPiece);

        }
        if (firstMove) {//first move has its own resolve method
            if(players[counter % 2] instanceof HumanPlayer && !resolveFirstMove(x, y))
                return;
        }
        firstMove = false;


        Point temp = players[counter % 2].resolveMove(new Point(y, x));//returns the point that the piece should be moved to
        Log.v("temp", temp.x + " " + temp.y);
        if (!temp.equals(inValid)) {//check validity
            if (selectedPiece.getRank() > 0 && temp.equals(players[counter % 2].getSumoPushPoint())) {//if it's sumo:
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
            win = GameLogic.win(board);
            if (!win.equals(-1, -1)) {//if someone won:
                Piece winPiece = board.getTile(win.x, win.y).getPiece();
                int winPlayer = winPiece.getOwner();
                score[winPlayer] += scores[winPiece.getRank()];
                gameBoardView.updateScore(score);
                board.rankUp(winPiece.getY(), winPiece.getX());
            }
            resolveNormalMove(temp.y, temp.x);
            if (!win.equals(-1, -1)) {
                Log.v("game", "somebody has won");
                counter = board.getTile(win.x, win.y).getPiece().getOwner();
                if (players[counter % 2] instanceof AIPlayer) {
                    aiWin = true;
                    Log.v("AITEST", "win called");
                }

            } else {
                if (players[counter % 2] instanceof AIPlayer && win.equals(-1, -1))
                    onTouch(-1, -1);
            }


        }
    }

    public void reset() {//resets the game board
        counter = (board.getTile(win.x, win.y).getPiece().getOwner() + 1) % 2;
        win = new Point(-1, -1);
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
