package com.radianceTOPS.constrain;

import java.util.ArrayList;

/**
 * Created by neerajen on 11/05/15.
 */
public class MoveGroup {//holds a set of moves, used because there are scenarios such as sumoPushes which require multiple-move undos
    private ArrayList<Move> moves;
    private int counter;

    public MoveGroup(int counter) {
        this.counter = counter;
        moves = new ArrayList<Move>();
    }

    public MoveGroup(Move move, int counter) {
        moves = new ArrayList<Move>();
        this.counter = counter;
        moves.add(move);
    }

    public void add(Move move) {
        moves.add(move);
    }

    public int size() {
        return moves.size();
    }

    public Move get(int position) {
        return moves.get(position);
    }

    public MoveGroup reverse() {
        MoveGroup reversed = new MoveGroup(counter);
        for (int i = moves.size() - 1; i >= 0; i--) {
            reversed.add(moves.get(i).reverse());
        }

        return reversed;
    }

    public int getCounter() {
        return counter;
    }
}
