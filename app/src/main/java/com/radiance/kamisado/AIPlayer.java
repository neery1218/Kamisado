package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

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
        if(difficulty == 0){
            int index = (int) (Math.random() * availMoves.size());
            return availMoves.get(index);
        }
        return new Point(-1, -1);
    }
}
