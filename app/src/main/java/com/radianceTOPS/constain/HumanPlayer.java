package com.radianceTOPS.constain;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class HumanPlayer extends Player {//humanPlayer

    public HumanPlayer() {
        super();
    }

    public HumanPlayer(int id) {
        super(id);
    }

    @Override
    public Point resolveMove(Point point) {//checks if the touchedPoint is an element in the availMoves array
        for (int i = 0; i < availMoves.size(); i++) {
            if (point.equals(availMoves.get(i)))
                return point;
        }
        return new Point(-1, -1);
    }

}
