package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Admin on 4/10/2015.
 */
public class AIPlayer extends Player {//AI player

    int MAX_DEPTH;
    private int difficulty = 0;
    private Board temp;

    public AIPlayer(int difficulty, int id) {//basic constructor
        super(id);
        this.difficulty = difficulty;
        MAX_DEPTH = 6;
    }

    public ArrayList<Point> nextMove(Board b, Point movePoint, int nextPlayer) {
        ArrayList<Point> test = calcMoves(b, nextPlayer, GameLogic.findPiece(b, nextPlayer, b.getColor(movePoint)));
        return test;
    }

    public Point difficulty0(){

        int distance = 0;

        for (int i = 0; i < availMoves.size(); i++) {
            if (hasPlayerWinMove(availMoves.get(i)))
                return availMoves.get(i);
        }

        Point maxPoint = new Point(-1, -1);
        int maxValue = -1, curValue = 0;

        for (int i = 0; i < availMoves.size(); i++) {
            curValue = 0;
            temp = new Board(board);
            temp.move(new Point(selectedPiece.getY(), selectedPiece.getX()), availMoves.get(i));
            int curColor = temp.getTile(availMoves.get(i).x, availMoves.get(i).y).getColor();
            Piece selectedPiece2 = GameLogic.findPiece(temp, this.player + 1, curColor);
            ArrayList<Point> opponentMove = nextMove(temp, availMoves.get(i), player + 1);

            if (opponentMove.size() == 0) {//if the opponent can move, curValue gets added five points because it's favourable
                curValue += 50;
            }

            for (int j = 0; j < opponentMove.size(); j++) {//but if opponent can win, subtract 100 from curValue
                if (hasOpponentWinMove(opponentMove.get(j))) {
                    curValue -= 100;
                    continue;
                }
            }
            Point openings = GameLogic.findOpenings(temp);//using gamelogic method to find openings
            int difference = openings.x - openings.y; //number of openings for player one - number of openings for player two
            curValue += difference;
            if (i == 0) {//default setting
                maxPoint = availMoves.get(i);
                maxValue = curValue;
            }
            if (curValue > maxValue) {
                maxPoint = availMoves.get(i);
                maxValue = curValue;
            }
            else if (curValue == maxValue) {
                double random = Math.random();
                if (random > 0.95) {
                    maxPoint = availMoves.get(i);
                    maxValue = curValue;
                }
            }
        }
        return maxPoint;
    }

    public Point difficulty1() {
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
            int curColor = temp.getTile(availMoves.get(i).x, availMoves.get(i).y).getColor();
            Piece selectedPiece2 = GameLogic.findPiece(temp, this.player + 1, curColor);
            ArrayList<Point> opponentMove = nextMove(temp, availMoves.get(i), player + 1);
            if(opponentMove.size() == 0){
                curValue += 3;
            }
            for (int j = 0; j < opponentMove.size(); j++) {
                if (hasOpponentWinMove(opponentMove.get(j))) {
                    curValue -= 100;
                    continue;
                }
                Board temp2 = new Board(temp);
                temp2.move(new Point(selectedPiece2.getPoint().y, selectedPiece2.getPoint().x), opponentMove.get(j));
                //Log.d("AI TEST", selectedPiece.getPoint() + " " + selectedPiece2.getPoint() + " " + opponentMove.get(j));

                ArrayList<Point> playerMove = nextMove(temp2, opponentMove.get(j), player);

                //Log.d("MOVES", "OPPONENT " + selectedPiece2.getPoint() + " " + availMoves.get(i) + " " +  opponentMove.get(j));
                /*for(int k = 0; k < playerMove.size(); k++){
                    Log.d("MOVES", "opponent " + playerMove.get(k).toString());
                }*/

                for(int k = 0; k < playerMove.size(); k++){
                    if(hasPlayerWinMove(playerMove.get(k))){
                        /*Log.d("MOVES", "WIN" +  playerMove.get(k).x + " " + playerMove.get(k).y + " " + opponentMove.get(j).x + " " + opponentMove.get(j).y);
                        for(int m = 0; m < 8; m++){
                            String s = "";
                            for(int l = 0; l < 8; l++){
                                if(temp2.getTile(m,l).isEmpty())
                                    s+="0";
                                else
                                    s+="1";
                            }
                            Log.d("MOVES", s);
                        }*/
                        curValue++;
                    }
                }
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

    public Point difficulty2() {//if there is a winning move, it takes it, otherwise it returns a random move
        int distance = 0;

        for (int i = 0; i < availMoves.size(); i++) {//returns winning move immediately
            if (hasPlayerWinMove(availMoves.get(i)))
                return availMoves.get(i);
        }

        Point maxPoint = new Point(-1, -1);
        double maxValue = -1, curValue = 0;
        for(int i = 0; i < availMoves.size(); i++){
            curValue = 0;
            temp = new Board(board);
            temp.move(new Point(selectedPiece.getY(), selectedPiece.getX()), availMoves.get(i));
            int curColor = temp.getTile(availMoves.get(i).x, availMoves.get(i).y).getColor();
            Piece selectedPiece2 = GameLogic.findPiece(temp, this.player + 1, curColor);
            ArrayList<Point> opponentMove = nextMove(temp, availMoves.get(i), player + 1);

            if (opponentMove.size() == 0) {//no moves for the opponent is good
                curValue += 100;
            }
            if(GameLogic.findBlocks(board, PLAYER_ONE) < GameLogic.findBlocks(temp, PLAYER_ONE))
                curValue += 100;
            for (int j = 0; j < opponentMove.size(); j++) {
                if (hasOpponentWinMove(opponentMove.get(j))) {//if they have a win, that's worst-case scenario
                    curValue -= 10000;
                    continue;
                }
                Board temp2 = new Board(temp);
                temp2.move(new Point(selectedPiece2.getPoint().y, selectedPiece2.getPoint().x), opponentMove.get(j));
                //Log.d("AI TEST", selectedPiece.getPoint() + " " + selectedPiece2.getPoint() + " " + opponentMove.get(j));

                ArrayList<Point> playerMove = nextMove(temp2, opponentMove.get(j), player);
                if (playerMove.size() == 0)//if they can move to a position where ai has no moves, that's really bad, pretty much a guaranteed win
                    curValue-= 500;

                //Log.d("MOVES", "OPPONENT " + selectedPiece2.getPoint() + " " + availMoves.get(i) + " " +  opponentMove.get(j));
                /*for(int k = 0; k < playerMove.size(); k++){
                    Log.d("MOVES", "opponent " + playerMove.get(k).toString());
                }*/

                for (int k = 0; k < playerMove.size(); k++) {//kinda useless?
                    if(GameLogic.findBlocks(temp, PLAYER_TWO) < GameLogic.findBlocks(temp2, PLAYER_TWO))
                        curValue -= 100;
                    /*if(GameLogic.findBlocks(temp) < GameLogic.findBlocks(temp2))
                        curValue += 100;*/
                    if (hasPlayerWinMove(playerMove.get(k))) {//if ai has win upon player's move

                        curValue+=10.0/playerMove.size();
                    }
                }
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
                if (random > 0.75) {
                    maxPoint = availMoves.get(i);
                    maxValue = curValue;
                }
            }
        }
        return maxPoint;
    }

    @Override
    public Point selectPiece(Board board) {
        Point A = new Point(0, 0);//assuming it's player two
        Point firstMove = new Point(1, 0);
        int enemyRow = boardDimension - 1;

        for (int j = 1; j < 5; j++) {
            for (int i = 1; i < board.getHeight() - 1; i++) {
                int enemyColor = board.getTile(enemyRow, i).getPiece().getColor();
                //check forward only on j = 1
                if (j == 1 && valid(enemyRow - j) && board.getTile(enemyRow - j, i).getColor() == enemyColor)
                    return new Point(0, i);

                if (valid(enemyRow - j) && valid(i - j) && board.getTile(enemyRow - j, i - j).getColor() == enemyColor)
                    return new Point(0, i);

                if (valid(enemyRow - j) && valid(i + j) && board.getTile(enemyRow - j, i + j).getColor() == enemyColor)
                    return new Point(0, i);

            }
        }

        calcMoves(board, player, board.getTile(A.x, A.y).getPiece());
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

    private int heuristicValue(Board board, int turn) {


        /*if (GameLogic.findBlocks(board, PLAYER_ONE) < GameLogic.findBlocks(board,PLAYER_TWO));
            return */

        //if turn is ai's and it has a blocked piece but they do too, go ahead
        Point openings = GameLogic.findOpenings(temp);//using gamelogic method to find openings
        return (openings.x - openings.y); //number of openings for player one - number of openings for player two


    }

    private int minMax(Board board, Piece selectedPiece, int depth, ArrayList<Point> availMoves) {

        //base cases

        if (depth == MAX_DEPTH)//depth level reached, return heuristic value of board state
            return heuristicValue(board, player);

        Point checkWin = GameLogic.win(board);
        if (!checkWin.equals(-1, -1)) {//if someone has won, return it

            if (board.getTile(checkWin).getPiece().getOwner() == PLAYER_TWO)//opponent
                return -10000;
            else
                return 10000;
        }

        if (availMoves.size() == 0) {//if player can't move
            selectedPiece = GameLogic.findPiece(board, selectedPiece.getOwner(), board.getTile(selectedPiece.getY(), selectedPiece.getX()).getColor());
            //ArrayList<Point> nextMove = findNextMoves(board, selectedPiece);
            ArrayList<Point> nextMove = new ArrayList<Point>();

            if (nextMove.size() == 0)
                return 0;

            else
                return minMax(board, selectedPiece, depth, nextMove);
        }

        if (selectedPiece.getOwner() == PLAYER_ONE) {//AI
            int bestValue = -10000;
            ArrayList<Integer> values = new ArrayList<Integer>();
            for (int i = 0; i < availMoves.size(); i++) {
                Board temp = new Board(board);
                temp.move(new Point(selectedPiece.getY(), selectedPiece.getX()), availMoves.get(i));
                values.add(minMax(board, selectedPiece, depth + 1, availMoves));//this is wrong, availMoves should be nextMoves
            }

            bestValue = Collections.max(values);
            return bestValue;
        } else {//opponent
            ArrayList<Integer> values = new ArrayList<Integer>();
            int bestValue = 10000;
            for (int i = 0; i < availMoves.size(); i++) {
                Board temp = new Board(board);
                temp.move(new Point(selectedPiece.getY(), selectedPiece.getX()), availMoves.get(i));
                values.add(minMax(board, selectedPiece, depth + 1, availMoves));//this is wrong, availMoves should be nextMoves
            }

            bestValue = Collections.min(values);
            return bestValue;
        }
    }





    @Override
    public Point resolveMove(Point point) {//overridden method, returns a move based on difficulty
        if(difficulty == 0){
            return difficulty0();
        }
        else if(difficulty == 1){
            return difficulty1();
        } else if (difficulty == 2) {
            return difficulty2();
        }
        return new Point(-1, -1);
    }


}
