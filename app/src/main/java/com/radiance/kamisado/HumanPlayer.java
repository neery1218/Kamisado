package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class HumanPlayer extends Player{

    public HumanPlayer(){

    }

    @Override
    public Point resolveMove() {
        //in Human Player, resolveMove will be passed selectedX and Y and it will determine if it's a correct move
        return new Point(0, 0);
    }

}
