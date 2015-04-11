package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class HumanPlayer extends Player{

    public HumanPlayer(){
        super();
    }

    public HumanPlayer(int id) {
        super(id);
    }

    @Override
    public Point resolveMove(Point point) {
        //in Human Player, resolveMove will be passed selectedX and Y and it will determine if it's a correct move
        //in AI Player, resolveMove will be called right after selected Move
        //in online Player, ...have to figure this one out

        for (int i = 0; i < availMoves.size(); i++) {
            if (point.equals(availMoves.get(i)))
                return point;
        }
        return new Point(-1, -1);
    }

}
