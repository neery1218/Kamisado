package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by neerajen on 10/05/15.
 */
public class Move {
    Point init;
    Point fin;

    public Move(Point init, Point fin) {
        this.init = init;
        this.fin = fin;
    }

    public Move reverse() {
        return new Move(fin, init);
    }
}
