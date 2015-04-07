package com.radiance.kamisado;

/**
 * Created by neerajen on 06/04/15.
 */
public class Tile {
    private int color;
    private Piece piece; //easily moved
    private int r;
    private int c;

    public Tile(int color, Piece piece, int r, int c) {
        this.color = color;
        this.piece = piece;
    }

    public Tile(int color, int r, int c) {
        this.color = color;
        this.piece = null;
    }

    public int getColor() {
        return color;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {

        this.piece = piece;
        piece.setLoc(c, r);
    }

    public boolean isEmpty() {

        return (piece == null);
    }

    public void pop() {
        piece = null;
    }

    public void rankUp (){
        piece.rankUp();
    }
}
