package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player {//AI player

    private int difficulty = 0;

    public AIPlayer(int difficulty, int id) {//basic constructor
        super(id);
        this.difficulty = difficulty;
    }

    public Point difficulty0(){
        Point p = new Point();
        int i = (int) (Math.random() * availMoves.size());
        return availMoves.get(i);
    }

    public Point difficulty1() {//if there is a winning move, it takes it, otherwise it returns a random move
        int distance = 0;
        for(int i = 0; i < availMoves.size(); i++){
            if(super.player == PLAYER_ONE && availMoves.get(i).x == 7){
                return availMoves.get(i);
            }
            else if(super.player == PLAYER_TWO && availMoves.get(i).x == 0){
                return availMoves.get(i);
            }
            findNextMoves(board, (super.player == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE), board.getTile(availMoves.get(i).y, availMoves.get(i).y).getPiece());
        }
        return difficulty0();
    }

    @Override
    public Point selectPiece(Board board) {
        Point A;
        if (board.getTile(boardDimension - 1, 0).getPiece().getOwner() == player)
            A = new Point(boardDimension - 1, 0);
        else
            A = new Point(0, 0);

        calcMoves(board, board.getTile(A.x, A.y).getPiece());
        return A;
    }


    @Override
    public Point resolveMove(Point point) {//overridden method, returns a move based on difficulty
        if(difficulty == 0){
            return difficulty1();//TODO: configure AI skill levels
        }
        else if(difficulty == 1){
            return difficulty1();
        }
        return new Point(-1, -1);
    }
}
