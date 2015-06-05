package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by neerajen on 10/05/15.
 */
public class Move {
    Point start;
    Point finish;

    public Move(Point init, Point fin) {
        this.start = init;
        this.finish = fin;
    }

    public Move reverse() {
        return new Move(finish, start);
    }

    public boolean equals(Move b) {
        return (start.equals(b.start) && finish.equals(b.finish));
    }

    public Point getFinish(){return start;}

    public Point getStart(){return finish;}
}
