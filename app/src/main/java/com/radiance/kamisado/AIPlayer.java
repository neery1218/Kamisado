package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player {//AI player

    private int difficulty = 1;

    public AIPlayer(int difficulty, int id) {//basic constructor
        super(id);
        this.difficulty = difficulty;
    }

    @Override
    public Point selectPiece(Board board) {
        Point A;
        if (board.getTile(boardDimension - 1, 0).getPiece().getOwner() == player)
            A = new Point(boardDimension - 1, 0);
        else
            A = new Point(0, 0);

        calcMoves(board, board.getTile(A.x, A.y).getPiece());
        return A;
    }


    @Override
    public Point resolveMove(Point point) {//overridden method, returns a move based on difficulty
        if(difficulty == 0){
            int index = (int) (Math.random() * availMoves.size());
            return availMoves.get(index);
        }
        return new Point(-1, -1);
    }
}
