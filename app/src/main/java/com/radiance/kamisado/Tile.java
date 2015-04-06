package com.radiance.kamisado;

/**
 * Created by neerajen on 06/04/15.
 */
public class Tile {
    private int color;
    private Piece piece; //easily moved

    public Tile(int color, Piece piece) {
        this.color = color;
        this.piece = piece;
    }

    public Tile(int color) {
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
    }

    public boolean isEmpty() {
        if (piece != null)
            return true;
        else
            return false;
    }

    public void pop() {
        piece = null;
    }
}
