package com.radiance.kamisado;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

/**
 * Created by neerajen on 06/04/15.
 */
public class Board {
    private Tile[][] board;
    private int[][] boardColor;
    private int boardDimension = 8;
    private Point[][] collected = new Point[2][boardDimension];
    private int PLAYER_TWO = 1, PLAYER_ONE = 0;
    private int[] colors = {Color.RED, Color.parseColor("#ED872D"), Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.parseColor("#69359C"), Color.parseColor("#FFB7C5"),
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];

    public Board() {
        boardColor = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        board = new Tile[8][8];

        //set tile colors
        for (int i = 0; i < board.length; i++)//row
            for (int j = 0; j < board[i].length; j++) {//column
                board[i][j] = new Tile(boardColor[i][j], i, j);
            }

        //set pieces
        for (int i = 0; i < board[0].length; i++) {
            board[0][i].setPiece(new Piece(board[0][i].getColor(), PLAYER_ONE));
            board[board.length - 1][i].setPiece(new Piece(board[board.length - 1][i].getColor(), PLAYER_TWO));
        }
    }


    public int getWidth() {
        return board[0].length;
    }

    public int getHeight() {
        return board.length;
    }

    public void move(Point a, Point b) {
        if (!getTile(a).isEmpty()) {
            Piece temp = board[a.x][a.y].getPiece();
            board[a.x][a.y].pop();
            board[b.x][b.y].setPiece(temp);
        }


    }

    public void rankUp (int r, int c){
	    board[r][c].rankUp();
    }

    public int getColor(int r, int c) {
        return board[r][c].getColor();
    }

    public Tile getTile(int r, int c) {
        return board[r][c];
    }

    public Tile getTile(int PLAYER, int r, int c) {
        if (PLAYER == PLAYER_ONE) {
            r = boardDimension - 1 - r;
            c = boardDimension - 1 - c;
        }
        return board[r][c];
    }

    public Tile getTile(Point a) {
        return board[a.x][a.y];
    }

    public void search() {//computes for fill left and right

        int counter1 = 0;
        int counter2 = 0;

        for (int i = 0; i < boardDimension; i++)//finds all the pieces starting from the top left to the bottom right
            for (int j = 0; j < boardDimension; j++) {

                if (!getTile(i, j).isEmpty()) {
                    Piece temp = getTile(i, j).getPiece();
                    if (temp.getOwner() == PLAYER_TWO) {
                        collected[PLAYER_TWO][counter1] = new Point(temp.getY(), temp.getX());
                        counter1++;
                        Log.v("One", temp.getY() + " " + temp.getX());
                    } else {
                        collected[PLAYER_ONE][counter2] = new Point(temp.getY(), temp.getX());
                        counter2++;
                        Log.v("Two", temp.getY() + " " + temp.getX());
                    }

                }
            }

    }

    public void clear() {
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i].setPiece(null);
            }
        }
    }

    public void fillLeft() {

        Board temp = new Board();
        temp.clear();
        for (int i = 0; i < collected[0].length; i++) {
            temp.board[boardDimension - 1][i].setPiece(this.getTile(collected[PLAYER_TWO][i].x, collected[PLAYER_TWO][i].y).getPiece());
            temp.board[0][i].setPiece(this.getTile(collected[PLAYER_ONE][i].x, collected[PLAYER_ONE][i].y).getPiece());
        }
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i] = temp.board[j][i];
            }
        }
        Log.v("fill", "Left");
    }

    public void fillRight() {

        Board temp = new Board();
        temp.clear();
        for (int i = 0; i < collected[0].length; i++) {
            temp.board[boardDimension - 1][7 - i].setPiece(this.getTile(collected[PLAYER_TWO][i].x, collected[PLAYER_TWO][i].y).getPiece());
            temp.board[0][7 - i].setPiece(this.getTile(collected[PLAYER_ONE][i].x, collected[PLAYER_ONE][i].y).getPiece());
        }
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i] = temp.board[j][i];
            }
        }
        Log.v("fill", "Right");
    }

    public void flip(){
        Tile[][] temp = new Tile[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for (int j = 0; j < boardDimension; j++){
                temp[j][i] = board[boardDimension - 1 - j][boardDimension -1 - i];
            }
        }
        board = temp;
    }
}
