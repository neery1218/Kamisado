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

    @Override
    public Point resolveMove(Point point) {
        if(difficulty == 1){
        }
        return new Point();
    }
}
