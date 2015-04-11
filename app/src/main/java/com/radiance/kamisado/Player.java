package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
abstract class Player {
    private int player = -1;
    private int boardDimension = 8;
    private final int PLAYER_ONE = 0;
    private final int PLAYER_TWO = 1;
    private Board board;

    public Player(){

    }

    public Player(int id){
        this.player = id;
    }

    public Board turn(Board temp, ArrayList<Point> availMoves, int x, int y){
        this.board = temp;
        if(player == PLAYER_TWO){
            board.flip();
        }
        resolveMove();
        if(player == PLAYER_TWO){
            board.flip();
        }
        return board;
    }

    public void resolveMove(){

    }

    public int win(){
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {

            //check if player one has won
            Piece temp = board.getTile(0, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_ONE) {
                return PLAYER_ONE;
            }

            temp = board.getTile(boardDimension - 1, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_TWO) {
                return PLAYER_TWO;
            }


        }
        return -1;
    }

    public void getBoard(Board board){
        this.board = board;
    }

    public Board getBoard(){
        return board;
    }
}
