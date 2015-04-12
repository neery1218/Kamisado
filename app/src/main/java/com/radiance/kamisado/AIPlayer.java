package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player{

    private int difficulty = 1;
    public AIPlayer(int difficulty, int id){
        super(id);
        this.difficulty = difficulty;
    }

    @Override
    public Point resolveMove(Point point) {
        if(difficulty == 1){
            int index = (int) (Math.random() * availMoves.size());
            return availMoves.get(0);
        }
        return new Point();
    }
}
