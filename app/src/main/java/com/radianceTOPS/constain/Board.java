package com.radianceTOPS.constain;

import android.graphics.Color;
import android.graphics.Point;

import java.util.Stack;

/**
 * Created by neerajen on 06/04/15.
 */
public class Board implements Cloneable{//board object
    private Tile[][] board;
    private int[][] boardColor;
    private int boardDimension = 8;
    private Point[][] collected = new Point[2][boardDimension];
    private int PLAYER_TWO = GameControl.PLAYER_TWO, PLAYER_ONE = GameControl.PLAYER_ONE;
    private Stack<Move> moveStack;
    private int undoCount; //this is so that reverted moves don't get added to the stack again

    //red,orange,yellow,green,blue,
    private int[] colors = {Color.parseColor("#ffe74c3c"), //red
            Color.parseColor("#F89406"), //orange
            Color.parseColor("#F7CA18"),//yellow
            Color.parseColor("#ff2ecc71"),//green
            Color.parseColor("#ff3498db"),//blue
            Color.parseColor("#ff8e44ad"),//purple
            Color.parseColor("#D2527F"),//pink
            Color.parseColor("#ACA46F")};
    private int r = colors[1], o = colors[2], ye = colors[5], g = colors[0], b = colors[7], p = colors[6], pk = colors[4], br = colors[3];

    public Board() {
        undoCount = 0;
        moveStack = new Stack<Move>();
        boardColor = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        board = new Tile[8][8];

        //set tile colors
        for (int i = 0; i < board.length; i++)//row
            for (int j = 0; j < board[i].length; j++) {//column
                board[i][j] = new Tile(boardColor[i][j], i, j);
            }

        //set pieces
        for (int i = 0; i < board[0].length; i++) {
            board[0][i].setPiece(new Piece(board[0][i].getColor(), PLAYER_ONE));
            board[board.length - 1][i].setPiece(new Piece(board[board.length - 1][i].getColor(), PLAYER_TWO));
        }
    }

    public Board(Board temp){
        undoCount = 0;
        moveStack = new Stack<Move>();
        Tile[][] tiles = temp.getTiles();
        boardColor = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        board = tiles;
    }


    public int getWidth() {
        return board[0].length;
    }

    public int getHeight() {
        return board.length;
    }

    public void move(Point a, Point b) {//moves piece from point a to point b
        if (!getTile(a).isEmpty()) {
            Piece temp = board[a.x][a.y].getPiece();
            board[a.x][a.y].pop();
            board[b.x][b.y].setPiece(temp);
            if (undoCount == 0)
                moveStack.add(new Move(a, b));
            else
                undoCount--;

        }

    }

    public void move(Move move) {//overloaded method
        Point a = move.start;
        Point b = move.finish;
        if (!getTile(a).isEmpty()) {
            Piece temp = board[a.x][a.y].getPiece();
            board[a.x][a.y].pop();
            board[b.x][b.y].setPiece(temp);
            if (undoCount == 0)
                moveStack.add(new Move(a, b));
            else
                undoCount--;

        }
    }

    public void move(MoveGroup moveGroup) {
        for (int i = 0; i < moveGroup.size(); i++) {
            move(moveGroup.get(i));
        }
    }

    public Move undo() {//return move that has to be executed
        Move undo = new Move(new Point(-1, -1), new Point(-1, -1));
        if (!moveStack.empty()) {
            undoCount++;
            undo = moveStack.pop().reverse();
            move(undo.start, undo.finish);
            return undo;
        }
        return undo;


    }

    public void rankUp(int r, int c) {//increases rank of a piece
        board[r][c].rankUp();
    }

    public int getColor(int r, int c) {
        return board[r][c].getColor();
    }

    public Tile getTile(int r, int c) {
        return board[r][c];
    }

    public Tile getTile(int PLAYER, int r, int c) {
        if (PLAYER == PLAYER_ONE) {
            r = boardDimension - 1 - r;
            c = boardDimension - 1 - c;
        }
        return board[r][c];
    }

    public Tile getTile(Point a) {
        return board[a.x][a.y];
    }

    public Tile [] [] getTiles (){
        Tile [] [] temp = new Tile [8][8];
        for (int i = 0; i < temp.length; i++){
            for (int j = 0; j < temp[0].length; j++){
                int a = board[i][j].getColor();
                temp[i][j] = new Tile (a,i,j);
                if (!board[i][j].isEmpty()){
                    int pieceColor = board[i][j].getPiece().getColor();
                    int rank = board[i][j].getPiece().getRank();
                    int owner = board[i][j].getPiece().getOwner();
                    Piece piece = new Piece(pieceColor, owner,rank);
                    temp[i][j].setPiece(piece);
                }
            }
        }

        return temp;
    }

    public void setTiles(Tile[][] tiles) {
        this.board = tiles;
    }

    public int getColor(Point p){
        return board[p.x][p.y].getColor();
    }

    public void search() {//computes for fill left and right

        int counter1 = 0;
        int counter2 = 0;

        for (int i = 0; i < boardDimension; i++)//finds all the pieces starting from the top left to the bottom right
            for (int j = 0; j < boardDimension; j++) {

                if (!getTile(i, j).isEmpty()) {
                    Piece temp = getTile(i, j).getPiece();
                    if (temp.getOwner() == PLAYER_TWO) {
                        collected[PLAYER_TWO][counter1] = new Point(temp.getY(), temp.getX());
                        counter1++;
                    } else {
                        collected[PLAYER_ONE][counter2] = new Point(temp.getY(), temp.getX());
                        counter2++;
                    }

                }
            }

    }

    public void clear() {
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i].setPiece(null);
            }
        }
    }

    public void fillLeft() {
        undoCount = 0;
        moveStack = new Stack<Move>();

        Board temp = new Board();
        temp.clear();
        for (int i = 0; i < collected[0].length; i++) {
            temp.board[boardDimension - 1][i].setPiece(this.getTile(collected[PLAYER_TWO][i].x, collected[PLAYER_TWO][i].y).getPiece());
            temp.board[0][i].setPiece(this.getTile(collected[PLAYER_ONE][i].x, collected[PLAYER_ONE][i].y).getPiece());
        }
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i] = temp.board[j][i];
            }
        }
    }

    public void fillRight() {
        undoCount = 0;
        moveStack = new Stack<Move>();
        Board temp = new Board();
        temp.clear();
        for (int i = 0; i < collected[0].length; i++) {
            temp.board[boardDimension - 1][7 - i].setPiece(this.getTile(collected[PLAYER_TWO][i].x, collected[PLAYER_TWO][i].y).getPiece());
            temp.board[0][7 - i].setPiece(this.getTile(collected[PLAYER_ONE][i].x, collected[PLAYER_ONE][i].y).getPiece());
        }
        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                board[j][i] = temp.board[j][i];
            }
        }
    }

    public void flip() {//used in player logic in order to retain calcMoves algorithm across all orientations
        Tile[][] temp = new Tile[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for (int j = 0; j < boardDimension; j++){
                temp[j][i] = board[boardDimension - 1 - j][boardDimension -1 - i];
            }
        }
        board = temp;
    }

    public Board inverse(){
        Tile[][] temp = new Tile[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for (int j = 0; j < boardDimension; j++){
                temp[j][i] = board[boardDimension - 1 - j][boardDimension -1 - i];
            }
        }
        Board b = new Board(this);
        b.setTiles(temp);
        return b;
    }

    @Override
    public Object clone (){
        try{
            return super.clone();
        }catch(Exception e){return null;}
    }
}
