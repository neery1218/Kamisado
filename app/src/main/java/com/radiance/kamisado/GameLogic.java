package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class GameLogic implements GameBoardView.OnBoardEvent {
    private static boolean firstMove = true;
    private final int PLAYER_TWO = 0;
    private final int PLAYER_ONE = 1;
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

    public GameLogic(GameBoardView gameBoardView, int bd, int VERSUS_TYPE) {
        this.boardDimension = bd;
        this.gameBoardView = gameBoardView;
        players = new Player[2];
        board = new Board();
        players[PLAYER_ONE] = new HumanPlayer(PLAYER_ONE);
        switch (VERSUS_TYPE) {
            case MainActivity.TWO_PLAY_PRESSED:
                players[PLAYER_TWO] = new HumanPlayer(PLAYER_TWO);
                Log.v("Game", "HumanPlayer");
                break;
            case MainActivity.PLAY_PRESSED:
                players[PLAYER_TWO] = new AIPlayer(EASY);
                Log.v("Game", "AIPlayer");
                break;
        }
        currColor = board.getColor(boardDimension - 1, 0);
        findPiece(counter % 2);

        selectedPiece = null;
        availMoves = new ArrayList<>();
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

    }

    private void win() {

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

    public void resolveSumoPushP1() {
        //find pieces that are gonna get sumo pushed
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));

        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
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

    public void resolveSumoPushP2() {

        //Pushing from the other end so it doesn't get overwritten
        for (int j = sumoChain; j >= 1; j--)
            board.move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX()));


        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX()));

    }

    public int getWin() {
        return win;
    }

    @Override
    public void onTouch(int x, int y) {
        if (players[counter % 2] instanceof AIPlayer)//clicks by other player during AI move
            return;

        if (firstMove) {
            if (!board.getTile(y, x).isEmpty()) {
                Log.d("debug", "called");
                selectedPiece = board.getTile(y, x).getPiece();
                availMoves = players[counter % 2].calcMoves(board, selectedPiece);
                gameBoardView.setSelectedPiece(selectedPiece);
                gameBoardView.setAvailMoves(availMoves);
                gameBoardView.drawBoard(board);
                return;
            } else if (selectedPiece == null)
                return;
        }

        availMoves = players[counter % 2].calcMoves(board, selectedPiece);
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board);

        Point temp = players[counter % 2].resolveMove(new Point(y, x));
        if (!temp.equals(inValid)) {
            if (selectedPiece.getRank() > 0 && temp.equals(players[counter % 2].getSumoPushPoint())) {
                sumoChain = players[counter % 2].getSumoChain();
                switch (counter % 2) {
                    case PLAYER_ONE:
                        resolveSumoPushP1();
                        break;
                    case PLAYER_TWO:
                        resolveSumoPushP2();
                        break;

                }
                counter++;
            } else
                board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), temp);
            counter++;

            //find next piece
            currColor = board.getColor(y, x);
            findPiece(counter % 2);
            availMoves = players[counter % 2].calcMoves(board, selectedPiece);
            if (players[counter % 2] instanceof HumanPlayer) {
                gameBoardView.setAvailMoves(availMoves);
                Log.d("debug", availMoves.size() + "");

            }

            gameBoardView.drawBoard(board);
            if (players[counter % 2] instanceof AIPlayer) {
                Point tempA = players[counter % 2].resolveMove();

                board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), tempA);
                counter++;

                //find next piece
                currColor = board.getColor(y, x);
                findPiece(counter % 2);
                availMoves = players[counter % 2].calcMoves(board, selectedPiece);
                gameBoardView.setAvailMoves(availMoves);
                Log.d("debug", availMoves.size() + "");
                gameBoardView.drawBoard(board);
            }
        }
        firstMove = false;
        win();
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
