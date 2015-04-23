package com.radiance.kamisado;

import android.util.Log;

/**
 * Created by Michael on 4/22/2015.
 */
public abstract class GameLogic {

    public static Piece findPiece(Board board, int player, int currColor){
        for (int i = 0; i < board.getHeight(); i++)
            for (int j = 0; j < board.getWidth(); j++) {
                Piece temp = board.getTile(i, j).getPiece();
                if (temp != null && temp.getColor() == currColor && temp.getOwner() == player) {
                    return temp;
                }
            }
        return null;
    }
}
