package com.radiance.kamisado;

import android.graphics.Point;

/**
 * Created by Michael on 4/22/2015.
 */
public abstract class GameLogic {//contains methods used by gameControl, and all Player sub-classes to conduct game logic

    private static final int PLAYER_TWO = GameControl.PLAYER_TWO, PLAYER_ONE = GameControl.PLAYER_ONE;
    private static int[] scores = {1, 3, 7, 15};

    public static Piece findPiece(Board board, int player, int currColor) {//find the next piece that must be moved
        for (int i = 0; i < board.getHeight(); i++)
            for (int j = 0; j < board.getWidth(); j++) {
                Piece temp = board.getTile(i, j).getPiece();
                if (temp != null && temp.getColor() == currColor && temp.getOwner() == player) {
                    return temp;
                }
            }
        return null;
    }

    public static Point win(Board board) {//checks if a player has won
        int win = 0, boardDimension = 8;
        //check if pieces have reached opposite side
        for (int i = 0; i < boardDimension; i++) {
            //check if player one has won
            Tile temp = board.getTile(0, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_TWO) {
                return new Point(0, i);
            }

            temp = board.getTile(boardDimension - 1, i);
            if (!temp.isEmpty() && temp.getPiece().getOwner() == PLAYER_ONE) {
                return new Point(boardDimension - 1, i);
            }
        }
        return new Point(-1, -1);
    }//Check for win. Return 1 if player 1 won, 0 if player 2 won, -1 if no one won yet

    public static boolean valid(int a) {
        return (a >= 0 && a < 7);
    }//Finds available moves of each player


    public static int findBlocks(Board board, int player) {
        int blockedPieces = 0;

        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if (!board.getTile(i, j).isEmpty() && board.getTile(i, j).getPiece().getOwner() == player) {

                    int possibleBlocks = 0;
                    int blockedPaths = 0;
                    int rank = board.getTile(i, j).getPiece().getRank();

                    //regular pieces
                    if (valid(i - 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i - 1, j).isEmpty())
                            possibleBlocks--;
                    }

                    if (valid(i - 1) && valid(j - 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i - 1, j - 1).isEmpty())
                            possibleBlocks--;
                    }
                    if (valid(i - 1) && valid(j + 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i - 1, j + 1).isEmpty())
                            possibleBlocks--;
                    }

                    //single sumo
                    if (rank > 0 && valid(j + 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i, j + 1).isEmpty())
                            possibleBlocks--;
                    }
                    if (rank > 0 && valid(j - 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i, j - 1).isEmpty())
                            possibleBlocks--;
                    }

                    //double sumo
                    if (rank > 1 && valid(i + 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i + 1, j).isEmpty())
                            possibleBlocks--;
                    }
                    if (rank > 1 && valid(i + 1) && valid(j + 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i + 1, j + 1).isEmpty())
                            possibleBlocks--;
                    }
                    if (rank > 1 && valid(i + 1) && valid(j - 1)) {
                        possibleBlocks++;
                        if (!board.getTile(i + 1, j - 1).isEmpty())
                            possibleBlocks--;
                    }

                    if (possibleBlocks == 0)
                        blockedPieces++;


                }
            }
        }

        return blockedPieces;

    }
    public static Point findOpenings (Board board) {

        Point openings = new Point(0, 0); //openings.x is player one openings, openings.y = player two openings
        boolean[][] pieceCounted = new boolean[board.getHeight()][board.getWidth()];
        //TODO: openings = piecesOpen + 0.5 * (totalOpenings - piecesOpen) or something like that
        boolean forwardBlockedTwo = false;
        boolean leftBlockedTwo = false;
        boolean rightBlockedTwo = false;

        boolean forwardBlockedOne = false;
        boolean leftBlockedOne = false;
        boolean rightBlockedOne = false;

        for (int i = 0; i < pieceCounted.length; i++)
            for (int j = 0; j < pieceCounted[i].length; j++)
                pieceCounted[i][j] = false;
        for (int i = 0; i < board.getHeight(); i++) {
            if (board.getTile(0, i).isEmpty()) {//check for player two piece win openings
                int y = 0, x = i;
                for (int j = 1; j <= board.getHeight() - 1; j++) {
                    if (!forwardBlockedTwo && valid(y + j) && valid(x) && !board.getTile(y + j, x).isEmpty()) {//forwards
                        if (board.getTile(y + j, x).getPiece().getOwner() == PLAYER_TWO) {

                            if (pieceCounted[y + j][x])
                                openings.y++;
                            else
                                openings.y += 3;

                            pieceCounted[y + j][x] = true;
                        }
                            forwardBlockedTwo = true;
                    }

                    if (!leftBlockedTwo && valid(y + j) && valid(x + j) && !board.getTile(y + j, x + j).isEmpty()) {//left diagonal
                        if (board.getTile(y + j, x + j).getPiece().getOwner() == PLAYER_TWO) {

                            if (pieceCounted[y + j][x + j])
                                openings.y++;
                            else
                                openings.y += 3;

                            pieceCounted[y + j][x + j] = true;
                        }
                            leftBlockedTwo = true;
                    }

                    if (!rightBlockedTwo && valid(y + j) && valid(x - j) && !board.getTile(y + j, x - j).isEmpty()) {//right diagonal
                        if (board.getTile(y + j, x - j).getPiece().getOwner() == PLAYER_TWO) {

                            if (pieceCounted[y + j][x - j])
                                openings.y++;
                            else
                                openings.y += 3;

                            pieceCounted[y + j][x - j] = true;
                        }

                            rightBlockedTwo = true;
                    }

                }
            }
            if (board.getTile(board.getHeight() - 1, i).isEmpty()) {//check for player one piece openings
                int y = board.getHeight() - 1, x = i;
                for (int j = 1; j <= board.getHeight() - 1; j++) {
                    if (!forwardBlockedOne && valid(y - j) && valid(x) && !board.getTile(y - j, x).isEmpty()) {//forwards  (relative to that side)
                        if (board.getTile(y - j, x).getPiece().getOwner() == PLAYER_ONE) {

                            if (pieceCounted[y - j][x])
                                openings.x++;
                            else
                                openings.x += 3;

                            pieceCounted[y - j][x] = true;
                        }

                            forwardBlockedOne = true;
                    }

                    if (!leftBlockedOne && valid(y - j) && valid(x + j) && !board.getTile(y - j, x + j).isEmpty()) {//left diagonal
                        if (board.getTile(y - j, x + j).getPiece().getOwner() == PLAYER_ONE) {

                            if (pieceCounted[y - j][x + j])
                                openings.x++;
                            else
                                openings.x += 3;

                            pieceCounted[y - j][x + j] = true;
                        }

                            leftBlockedOne = true;
                    }

                    if (!rightBlockedOne && valid(y - j) && valid(x - j) && !board.getTile(y - j, x - j).isEmpty()) {//right diagonal
                        if (board.getTile(y - j, x - j).getPiece().getOwner() == PLAYER_ONE) {

                            if (pieceCounted[y - j][x - j])
                                openings.x++;
                            else
                                openings.x += 3;

                            pieceCounted[y - j][x - j] = true;
                        }
                            rightBlockedOne = true;
                    }
                }
            }


        }

        return openings;
    }
}
