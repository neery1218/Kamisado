package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player {//AI player

    private int difficulty = 0;

    public AIPlayer(int difficulty, int id) {//basic constructor
        super(id);
        this.difficulty = difficulty;
    }

    public Board nextMove(Board board, Point curPoint, Point movePoint) {//AI part of AIPlayer: returns a move based on difficulty
        board.move(curPoint, movePoint);
        int nextPlayer = (player == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE);
        ArrayList<Point> test = findNextMoves(board, nextPlayer, GameLogic.findPiece(board, nextPlayer, board.getColor(movePoint)));

        return board;
    }

    public Point difficulty0() {//easiest difficulty: returns a random move
        Point p = new Point();
        int i = (int) (Math.random() * availMoves.size());

        return availMoves.get(i);
    }

    public Point difficulty1() {//if there is a winning move, it takes it, otherwise it returns a random move
        int distance = 0;
        if (hasPlayerWinMove(availMoves))
            for (int i = 0; i < availMoves.size(); i++) {
                if (super.player == PLAYER_ONE && availMoves.get(i).x == 7) {
                    return availMoves.get(i);
                } else if (super.player == PLAYER_TWO && availMoves.get(i).x == 0) {
                    return availMoves.get(i);
                }
            }

        for (int i = 0; i < availMoves.size(); i++) {
            Board temp = nextMove(board, selectedPiece.getPoint(), availMoves.get(i));
        }
        return difficulty0();
    }

    @Override
    public Point selectPiece(Board board) {//selects a piece for first move: currently just selects the leftmost one
        Point A;
        if (board.getTile(boardDimension - 1, 0).getPiece().getOwner() == player)
            A = new Point(boardDimension - 1, 0);
        else
            A = new Point(0, 0);

        calcMoves(board, board.getTile(A.x, A.y).getPiece());
        return A;
    }

    public boolean hasOpponentWinMove(ArrayList<Point> p) {//checks if a move by AI Player results in a loss.
        for (int i = 0; i < p.size(); i++) {
            if ((p.get(i).x == 0 && this.player == PLAYER_TWO) || (p.get(i).x == 7 && this.player == PLAYER_ONE)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPlayerWinMove(ArrayList<Point> p) {//checks if a move results in a win
        for (int i = 0; i < p.size(); i++) {
            if ((p.get(i).x == 0 && this.player == PLAYER_ONE) || (p.get(i).x == 7 && this.player == PLAYER_TWO)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Point resolveMove(Point point) {//overridden method, returns a move based on difficulty
        if (difficulty == 0) {
            return difficulty1();//TODO: configure AI skill levels
        } else if (difficulty == 1) {
            return difficulty1();
        }
        return new Point(-1, -1);
    }
}
