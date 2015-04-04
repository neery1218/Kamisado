package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Admin on 4/4/2015.
 */
public class AI {

    public AI(){

    }

    public Point move(ArrayList<Point> availMoves){
        return availMoves.get((int)(Math.random()) * availMoves.size());
    }

}
