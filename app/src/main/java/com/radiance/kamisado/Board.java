package com.radiance.kamisado;

import android.graphics.Color;
import android.util.Log;

public class Board {
	int[][] board8Color;
	Piece[] p1, p2;
    private Piece[] temp1, temp2;
    private int[] colors = {Color.RED, Color.parseColor("#ED872D"), Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.parseColor("#69359C"), Color.parseColor("#FFB7C5"),
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];
    private GameBoard gameBoard;
    private int boardDimension = 8;

	public Board(GameBoard gameBoard, int bd){
        this.boardDimension = bd;
        this.gameBoard = gameBoard;
        board8Color = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        int[][] temp = new int[boardDimension][boardDimension];
        for(int i = 0; i < boardDimension; i++){
            for(int j = 0; j < boardDimension; j++){
                temp[i][j] = board8Color[j][i];

            }
        }

        board8Color = temp;
	}

    public void search(){
        temp1 = new Piece [boardDimension];
        temp2 = new Piece [boardDimension];
        int counter1 = 0;
        int counter2 = 0;
        for (int i = 0; i < boardDimension; i++)//finds all the pieces starting from the top left to the bottom right
            for (int j = 0; j < boardDimension; j++){
                for (int k = 0; k < p1.length; k++){
                    if (p1[k].getX() == j && p1[k].getY() == i){
                        temp1[boardDimension - 1 - counter1] = p1[k];
                        counter1++;
                    }
                    if (p2[k].getX() == j && p2[k].getY() == i){
                        temp2[boardDimension - 1 - counter2] = p2[k];
                        counter2++;
                    }
                }
            }
        //reverse temp1 onto p1
        Log.d("TAG", counter1 + " " + counter2);
    }

    public Piece[][] fillRight(Piece[] t1, Piece[] t2){

        p1 = t1; p2 = t2;
        search();
        p1 = new Piece[boardDimension];
        p2 = new Piece[boardDimension];
        for (int i = 0; i < boardDimension; i++) {
            p1[i] = new Piece(i, 0, temp1[boardDimension - 1 - i].getColor(), temp1[boardDimension - 1 - i].getRank());
            p2[i] = new Piece(i, boardDimension - 1, temp2[boardDimension - 1 - i].getColor(), temp2[boardDimension - 1 - i].getRank());
        }
        for(int i = 0; i < 8; i++){
            Log.d("TAG", p1[i].getX() + " " + p1[i].getY() + " " +  p2[i].getX() + " " + p2[i].getY());
        }
        Piece[][] a = new Piece[2][1];
        a[0] = p1;
        a[1] = p2;
        return a;
    }

    public Piece[][] fillLeft(Piece[] t1, Piece[] t2){

        this.p1 = t1; this.p2 = t2;
        search();
        p1 = new Piece[boardDimension];
        p2 = new Piece[boardDimension];
        for (int i = 0; i < boardDimension; i++) {
            p1[i] = new Piece(i, 0, temp1[i].getColor(), temp1[i].getRank());
            p2[i] = new Piece(i, boardDimension - 1, temp2[i].getColor(), temp2[i].getRank());
        }
        for(int i = 0; i < 8; i++){
            Log.d("TAG", p1[i].getX() + " " + p1[i].getY() + " " +  p2[i].getX() + " " + p2[i].getY());
        }
        Piece[][] a = new Piece[2][1];
        a[0] = p1;
        a[1] = p2;
        return a;
    }
}
