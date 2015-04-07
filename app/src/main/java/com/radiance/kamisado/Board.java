package com.radiance.kamisado;

import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by neerajen on 06/04/15.
 */
public class Board {
    private Tile[][] board;
    private int[][] boardColor;
    private int PLAYER_ONE = 1, PLAYER_TWO = 0;
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
            board[0][i].setPiece(new Piece(board[0][i].getColor(), PLAYER_TWO));
            board[board.length - 1][i].setPiece(new Piece(board[board.length - 1][i].getColor(), PLAYER_ONE));
        }
    }

    public int getWidth() {
        return board[0].length;
    }

    public int getHeight() {
        return board.length;
    }
    public void move(Point a, Point b) {
        board[b.x][b.y].setPiece(board[a.x][a.y].getPiece());
        board[a.x][a.y].pop();

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
}
