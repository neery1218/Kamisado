package com.radiance.kamisado;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
abstract class Player {//abstract class used to hold player logic and give way to AI and HumanPlayer and onlinePlayer subclasses

    protected final int PLAYER_TWO = GameControl.PLAYER_TWO;
    protected final int PLAYER_ONE = GameControl.PLAYER_ONE;

    protected int player = -1;
    protected int boardDimension = 8;

    protected Board board;

    protected int sumoChain = 0;
    protected Point sumoPushOption;
    protected ArrayList<Point> availMoves = new ArrayList<Point>();
    protected Piece selectedPiece;

    public Player(){
    }

    public Player(int id){
        this.player = id;
    }

    public Point resolveMove() {//move called by gamelogic to determine validity or to choose one from availMoves
        //in Human Player, resolveMove will be passed selectedX and Y and it will determine if it's a correct move
        //in AI Player, resolveMove will be called right after selected Move
        //in online Player, ...have to figure this one out
        return new Point(-1, -1);
    }

    public Point selectPiece(Board board) {
        return new Point();
    }

    public Point resolveMove(Point point){
        return new Point(-1, -1);
    }


    private boolean valid (int a){
        return (a >= 0 && a < boardDimension);
    }

    public ArrayList<Point> calcMoves(Board temp, Piece selectedPiece) {//determines all available moves
        sumoPushOption = new Point(-1, -1);
        if(player == PLAYER_ONE)
            temp.flip();

        this.board = temp;
        this.selectedPiece = selectedPiece;
        ArrayList<Point> availMoves = new ArrayList<>();

        //regular piece options
        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        //sumo options
        boolean leftSideWaysBlocked = false;
        boolean rightSideWaysBlocked = false;

        //double sumo options
        boolean backwardsBlocked = false;
        boolean backLeftDiagonalBlocked = false;
        boolean backRightDiagonalBlocked = false;

        //these have a different orientation
        int x = selectedPiece.getX();
        int y = selectedPiece.getY();
        if (player == PLAYER_ONE) {
            x = boardDimension - 1 - x;
            y = boardDimension - 1 - y;
        }


        for (int i = 1; i <= selectedPiece.getDistance(); i++) {

            //regular moves
            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board.getTile(y - i, x).isEmpty()) {
                    availMoves.add(new Point(y - i, x));
                } else if (selectedPiece.getRank() > 0 && i == 1) {//check for sumoPushes
                    int sumoCounter = 0;

                    while (valid(y - i - sumoCounter) && !board.getTile(y - i - sumoCounter, x).isEmpty() && board.getTile(y - i - sumoCounter, x).getPiece().getOwner() != player) {//checks for a chain of opponent pieces
                        if (board.getTile(y - i - sumoCounter, x).getPiece().getRank() >= selectedPiece.getRank()) {
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    //if the number of opponent pieces are less than the current piece's rank, and the square behind the chain is empty
                    if (valid(y - i - sumoCounter) && sumoCounter > 0 && sumoCounter <= selectedPiece.getRank() && board.getTile(y - i - sumoCounter, x).getPiece() == null) {
                        sumoPushOption = new Point(y - i - sumoCounter, x);
                        availMoves.add(sumoPushOption);//adds it as a valid move
                        sumoChain = sumoCounter;
                    }


                    forwardBlocked = true;
                }
                else
                    forwardBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(y - i) && valid(x - i)) {//left diagonal
                if (board.getTile(y - i, x - i).isEmpty())
                    availMoves.add(new Point(y - i, x - i));
                else
                    leftDiagonalBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + x) && valid(y - i)) {//right diagonal
                if (board.getTile(y - i, i + x).isEmpty())
                    availMoves.add(new Point(y - i, x + i));
                else
                    rightDiagonalBlocked = true;
            }

            //sumo moves
            if (selectedPiece.getRank() > 0 && !leftSideWaysBlocked && valid(i + x) && valid(y)) {//left Sideways
                if (board.getTile(y, i + x).isEmpty())
                    availMoves.add(new Point(y, x + i));
                else
                    leftSideWaysBlocked = true;
            }

            if (selectedPiece.getRank() > 0 && !rightSideWaysBlocked && valid(x - i) && valid(y)) {//right Sideways
                if (board.getTile(y, x - i).isEmpty())
                    availMoves.add(new Point(y, x - i));
                else
                    rightSideWaysBlocked = true;
            }

            //double sumo moves
            if (selectedPiece.getRank() > 1 && !backLeftDiagonalBlocked && valid(i + x) && valid(y + i)) {//back left diagonal
                if (board.getTile(y + i, i + x).isEmpty())
                    availMoves.add(new Point(y + i, x + i));
                else
                    backLeftDiagonalBlocked = true;
            }

            if (selectedPiece.getRank() > 1 && !backRightDiagonalBlocked && valid(x - i) && valid(y + i)) {//back right diagonal
                if (board.getTile(y + i, x - i).isEmpty())
                    availMoves.add(new Point(y + i, x - i));
                else
                    backRightDiagonalBlocked = true;
            }

            if (selectedPiece.getRank() > 1 && !backwardsBlocked && valid(x) && valid(y + i)) {//right Sideways
                if (board.getTile(y + i, x).isEmpty())
                    availMoves.add(new Point(y + i, x));
                else
                    backwardsBlocked = true;
            }

        }
        if(player == PLAYER_ONE) {
            for(int i = 0; i < availMoves.size(); i++){
                Point orient = availMoves.get(i);
                availMoves.set(i, new Point(boardDimension - 1 - orient.x, boardDimension - 1 - orient.y));
            }
            temp.flip();
            if (!sumoPushOption.equals(-1, -1)) {
                sumoPushOption = new Point(boardDimension - 1 - sumoPushOption.x, boardDimension - 1 - sumoPushOption.y);
            }
        }
        this.availMoves = availMoves;
        return availMoves;
    }//Finds available moves of each player

    public ArrayList<Point> findNextMoves(Board board, int player, Piece selectedPiece){
        if(player == PLAYER_ONE)
            board.flip();

        ArrayList<Point> availMoves = new ArrayList<>();

        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        //these have a different orientation
        int x = selectedPiece.getX();
        int y = selectedPiece.getY();
        if (player == PLAYER_ONE) {
            x = boardDimension - 1 - x;
            y = boardDimension - 1 - y;
        }


        for (int i = 1; i <= selectedPiece.getDistance(); i++) {
            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board.getTile(y - i, x).isEmpty()) {
                    availMoves.add(new Point(y - i, x));
                }
                else
                    forwardBlocked = true;
            }


            if (!leftDiagonalBlocked && valid(y - i) && valid(x - i)) {//left diagonal
                if (board.getTile(y - i, x - i).isEmpty())
                    availMoves.add(new Point(y - i, x - i));
                else
                    leftDiagonalBlocked = true;
            }

            if (!rightDiagonalBlocked && valid(i + x) && valid(y - i)) {//right diagonal
                if (board.getTile(y - i, i + x).isEmpty())
                    availMoves.add(new Point(y - i, x + i));
                else
                    rightDiagonalBlocked = true;
            }
        }
        if(player == PLAYER_ONE) {
            for(int i = 0; i < availMoves.size(); i++){
                Point orient = availMoves.get(i);
                availMoves.set(i, new Point(boardDimension - 1 - orient.x, boardDimension - 1 - orient.y));

            }
            board.flip();
        }
        return availMoves;
    }

    public Point getSumoPushPoint() {
        return sumoPushOption;
    }

    public int getSumoChain() {
        return sumoChain;
    }


}
