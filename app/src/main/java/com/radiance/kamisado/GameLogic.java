package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameLogic implements GameBoardView.OnBoardEvent {
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

    public GameLogic(GameBoardView gameBoardView, int bd, int VERSUS_TYPE) {
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
        findPiece(counter % 2, currColor);

        selectedPiece = null;
        availMoves = new ArrayList<>();
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

    }

    private void win() {
        Log.v("Game", "Win called");
        win = -1;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {

            //check if player one has won
            Tile temp = board.getTile(0, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_TWO) {
                Log.v("Game", "Rank: " + temp.getPiece().getRank());
                score[PLAYER_TWO] += scores[temp.getPiece().getRank()];
                board.rankUp(0, i);
                win = PLAYER_TWO;
                gameBoardView.updateScore(score);
            }

            temp = board.getTile(boardDimension - 1, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_ONE) {
                Log.v("Game", "Rank: " + temp.getPiece().getRank());
                score[PLAYER_ONE] += scores[temp.getPiece().getRank()];
                board.rankUp(boardDimension - 1, i);
                win = PLAYER_ONE;
                gameBoardView.updateScore(score);
            }


        }
    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet

    public void resolveSumoPushP1() {
        //find pieces that are gonna get sumo pushed
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));

        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
    }

    private void findPiece(int PLAYER, int currColor) {
        for (int i = 0; i < board.getHeight(); i++)
            for (int j = 0; j < board.getWidth(); j++) {
                Piece temp = board.getTile(i, j).getPiece();
                if (temp != null && temp.getColor() == currColor && temp.getOwner() == PLAYER) {
                    selectedPiece = temp;
                }

            }

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
        if (availMoves.size() != 0)
            win();
        currColor = board.getColor(y, x);
        findPiece(counter % 2, currColor);
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
        if (win != -1)
            availMoves = new ArrayList<Point>();
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
            resolveNormalMove(temp.y, temp.x);

            if (players[counter % 2] instanceof AIPlayer && win == -1) {
                onTouch(-1, -1);
                //TODO: win calling is all over the place, has to be fixed
            }
        }
    }

    public void reset() {
        counter = win;
        win = -1;
        firstMove = true;
        selectedPiece = null;
        gameBoardView.setSelectedPiece(null);
        if (players[counter % 2] instanceof AIPlayer) {
            Point A = players[counter % 2].selectPiece(board);
            firstMove = false;
            onTouch(A.y, A.x);


        }
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
