package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player{

    private int difficulty = 1;
    public AIPlayer(int difficulty){
        super();
        this.difficulty = difficulty;
    }


    public Point turn(Board temp, Piece selectedPiece){
        Point movePoint = new Point(-1, -1);
        if(player == PLAYER_TWO)
            temp.flip();
        resolveMove(null);
        return movePoint;
    }

    @Override
    public Point resolveMove(Point point) {
        if(difficulty == 1){
        }
        return new Point();
    }
}
