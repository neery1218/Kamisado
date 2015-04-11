package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
abstract class Player {
    private int boardDimension = 8;
    private final int PLAYER_ONE = 0;
    private final int PLAYER_TWO = 1;
    private Board board;
    private int[] score = new int[2];
    private int[] scores = {1, 3, 7, 15};

    public int turn(int counter, ArrayList<Point> availMoves, int x, int y){
        return win();
    }

    public int win(){
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {

            //check if player one has won
            Piece temp = board.getTile(0, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_ONE) {

                score[PLAYER_ONE] += scores[temp.getRank()];
                board.rankUp(0, i);
                return PLAYER_ONE;
            }

            temp = board.getTile(boardDimension - 1, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_TWO) {
                score[PLAYER_TWO] += scores[temp.getRank()];
                board.rankUp(boardDimension - 1, i);
                return PLAYER_TWO;
            }


        }
        return -1;
    }

    public void getBoard(Board board){
        this.board = board;
    }

    public void getScore(int[] score){
        this.score = score;
    }

    public Board getBoard(){
        return board;
    }

    public int[] score(){
        return score;
    }
}
