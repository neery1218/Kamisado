package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

/**
 * Created by Michael on 4/22/2015.
 */
public abstract class GameLogic {

    private static int[] scores = {1, 3, 7, 15};
    private static final int PLAYER_TWO = 1, PLAYER_ONE = 0;
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

    public static Point win(Board board) {
        int win = 0, boardDimension = 8;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            //check if player one has won
            Tile temp = board.getTile(0, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_TWO) {
                return new Point(0, i);
            }

            temp = board.getTile(boardDimension - 1, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_ONE) {
                return new Point(boardDimension - 1 - i, i);
            }
        }
        return new Point(-1, -1);
    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet
}
