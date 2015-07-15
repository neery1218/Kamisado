package com.radianceTOPS.constrain;

/**
 * Created by neerajen on 06/04/15.
 */
public class Tile {
    private int color;
    private Piece piece;
    private int r;
    private int c;
    //TODO: isEmpty boolean variable?

    public Tile(int color, Piece piece, int r, int c) {
        this.color = color;
        this.piece = piece;
        this.r = r;
        this.c = c;
    }

    public Tile(int color, int r, int c) {
        this.color = color;
        this.piece = null;
        this.r = r;
        this.c = c;
    }

    public int getColor() {
        return color;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {

        this.piece = piece;
        if (this.piece != null)
            piece.setLoc(c, r);
    }

    public boolean isEmpty() {

        try {
            if (piece.getX() == c && piece.getY() == r) {
                return false;
            }
        } catch (NullPointerException e) {
        }
        return true;
    }

    public void pop() {
        piece = null;
    }

    public void rankUp (){
        piece.rankUp();
    }
}
