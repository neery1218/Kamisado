package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player {//AI player

    private int difficulty = 0;
    private Board temp;

    public AIPlayer(int difficulty, int id) {//basic constructor
        super(id);
        this.difficulty = difficulty;
    }

    public ArrayList<Point> nextMove(Board b, Point movePoint) {
        int nextPlayer = (player == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE);
        ArrayList<Point> test = findNextMoves(b, nextPlayer, GameLogic.findPiece(b, nextPlayer, b.getColor(movePoint)));
        return test;
    }

    public Point difficulty0(){
        Point p = new Point();
        int i = (int) (Math.random() * availMoves.size());

        return availMoves.get(i);
    }

    public Point difficulty1() {//if there is a winning move, it takes it, otherwise it returns a random move
        int distance = 0;
        for(int i = 0; i < availMoves.size(); i++){
            if (hasPlayerWinMove(availMoves.get(i)))
                return availMoves.get(i);
        }

        Point maxPoint = new Point(-1, -1);
        int maxValue = -1, curValue = 0;
        for(int i = 0; i < availMoves.size(); i++){
            curValue = 0;
            temp = new Board(board);
            temp.move(new Point(selectedPiece.getY(), selectedPiece.getX()), availMoves.get(i));
            /*for(int k = 0; k < 8; k++){
                String s = "";
                for(int j = 0; j < 8; j++){
                    if(temp.getTile(k, j).isEmpty()){
                        s += 0;
                    }
                    else
                        s += 1;
                }
                Log.d("AITEST", s + " " + k);
            }
            Log.d("AITEST", "");*/
            ArrayList<Point> opponentMove = nextMove(temp, availMoves.get(i));
            for (int j = 0; j < opponentMove.size(); j++) {
                if (hasOpponentWinMove(opponentMove.get(j))) {
                    curValue -= 5   ;
                    Log.d("AITEST", "win detected" + " " + opponentMove.get(j).x + " " + opponentMove.get(j).y);
                    continue;
                }
                /*Board temp2 = new Board(temp);
                temp2.move(new Point(availMoves.get(i).y, availMoves.get(i).x), opponentMove.get(j));
                ArrayList<Point> playerMove = nextMove(temp2, opponentMove.get(j));
                for(int k = 0; k < playerMove.size(); k++){
                    if(hasPlayerWinMove(playerMove.get(k))){
                        Log.d("AITEST", "called");
                        curValue++;
                    }
                }*/
            }
            if (i == 0) {
                maxPoint = availMoves.get(i);
                maxValue = curValue;
            }
            if (curValue > maxValue) {
                maxPoint = availMoves.get(i);
                maxValue = curValue;
            } else if (curValue == maxValue) {
                double random = Math.random();
                if (random > 0.95) {
                    maxPoint = availMoves.get(i);
                    maxValue = curValue;
                }
            }
        }

        return maxPoint;
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

    public boolean hasOpponentWinMove(Point p){
        if((p.x == 7 && this.player == PLAYER_TWO) || (p.x == 0 && this.player == PLAYER_ONE)){
            return true;
        }
        return false;
    }

    public boolean hasPlayerWinMove(Point p){
        if((p.x == 7 && this.player == PLAYER_ONE) || (p.x == 0 && this.player == PLAYER_TWO)){
            return true;
        }
        return false;
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
