package com.radiance.kamisado;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Admin on 4/10/2015.
 */
abstract class Player {
    protected int player = -1;
    protected int boardDimension = 8;
    protected final int PLAYER_ONE = 0;
    protected final int PLAYER_TWO = 1;
    protected Board board;
    protected int sumoChain = 0;
    protected Point sumoPushOption;

    public Player(){

    }

    public Player(int id){
        this.player = id;
    }

    public Board turn(Board temp, Piece selectedPiece){
        this.board = temp;
        if(player == PLAYER_TWO){
            board.flip();
        }
        resolveMove(selectedPiece);
        if(player == PLAYER_TWO){
            board.flip();
        }
        return board;
    }

    public void resolveMove(Piece selectedPiece){
        ArrayList<Point> availMoves = search(selectedPiece);

    }

    public int win(){
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {

            //check if player one has won
            Piece temp = board.getTile(0, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_ONE) {
                return PLAYER_ONE;
            }

            temp = board.getTile(boardDimension - 1, i).getPiece();
            if (temp != null && temp.getOwner() == PLAYER_TWO) {
                return PLAYER_TWO;
            }


        }
        return -1;
    }

    private boolean valid (int a){
        return (a >= 0 && a < boardDimension);
    }//Finds available moves of each player

    private ArrayList<Point> search(Piece selectedPiece) {
        ArrayList<Point> availMoves = new ArrayList<>();

        boolean leftDiagonalBlocked = false;
        boolean rightDiagonalBlocked = false;
        boolean forwardBlocked = false;

        int x = selectedPiece.getX();
        int y = selectedPiece.getY();

        Log.v("GAT", "Current Distance:" + selectedPiece.getDistance() + " Rank:" + selectedPiece.getRank());

        for (int i = 1; i <= selectedPiece.getDistance(); i++) {
            if (!forwardBlocked && valid(y - i)) { //finds moves directly forward
                if (board.getTile(y - i, x).isEmpty()) {
                    availMoves.add(new Point(y - i, x));
                }

                else if (selectedPiece.getRank() > 0) {//check for sumoPushes

                    int sumoCounter = 0;

                    while (valid(y - i - sumoCounter) && !board.getTile(y - i - sumoCounter, x).isEmpty() && board.getTile(y - i - sumoCounter, x).getPiece().getOwner() == PLAYER_TWO) {//checks for a chain of opponent pieces
                        if (board.getTile(y - i - sumoCounter, x).getPiece().getRank() >= selectedPiece.getRank()) {
                            sumoCounter = 0;
                            break;
                        }
                        sumoCounter++;
                    }
                    Log.v("GAT", "counter:" + sumoCounter);
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

        }
        return availMoves;
    }//Search for moves for player 2

    public void resolveSumoPushP1(int x, Piece selectedPiece){
        //find pieces that are gonna get sumo pushed
        //make points
        for (int j = sumoChain; j >= 1; j--) {
            // findPieceAt (x,y+j);
            board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));
        }
        board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
    }

    public void getBoard(Board board){
        this.board = board;
    }

    public Board getBoard(){
        return board;
    }
}
