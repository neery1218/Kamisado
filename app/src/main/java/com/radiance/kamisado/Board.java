package com.radiance.kamisado;

import android.graphics.Color;

public class Board {
	int[][] board8Color;
	Piece[] p1, p2;
    private int[] colors = {Color.RED, Color.parseColor("#ED872D"), Color.YELLOW,
            Color.GREEN, Color.BLUE, Color.parseColor("#69359C"), Color.parseColor("#FFB7C5"),
            Color.parseColor("#964B00")};
    private int r = colors[0], o = colors[1], ye = colors[2], g = colors[3], b = colors[4], p = colors[5], pk = colors[6], br = colors[7];
	
	public Board(GameBoard m){
        board8Color = new int[][]{
                {o,b,p,pk,ye,r,g,br},
                {r,o,pk,g,b,ye,br,p},
                {g,pk,o,r,p,br,ye,b},
                {pk,p,b,o,br,g,r,ye},
                {ye,r,g,br,o,b,p,pk},
                {b,ye,br,p,r,o,pk,g},
                {p,br,ye,b,g,pk,o,r},
                {br,g,r,ye,pk,p,b,o}};

        int[][] temp = new int[board8Color.length][board8Color.length];
        for(int i = 0; i < board8Color.length; i++){
            for(int j = 0; j < board8Color.length; j++){
                temp[i][j] = board8Color[j][i];

            }
        }

        board8Color = temp;
	}
	
	public void resetLeft(){
		for(int i = 0; i < 8; i++){
			p1[i].setLoc(i, 0);
			p2[i].setLoc(i, 7);
		}
	}
    public void fillLeft(){
        Piece [] temp1 = new Piece [board8Color.length];
        Piece [] temp2 = new Piece [board8Color.length];
        int counter1 = 0;
        int counter2 = 0;
        for (int i = 0; i < board8Color.length; i++)//finds all the pieces starting from the top left to the bottom right
            for (int j = 0; j < board8Color.length; j++){
                for (int k = 0; k < p1.length; k++){
                    if (p1[k].getX() == j && p1[k].getY() == i){
                        temp1[counter1] = p1[k];
                        counter1++;
                    }
                    if (p2[k].getX() == j && p2[k].getY() == i){
                        temp2[counter2] = p1[k];
                        counter2++;
                    }
                }
            }
        //reverse temp2 onto p2
        p2 = new Piece[board8Color.length];
        for (int i = 0; i < p2.length; i++)
            p2[i] = temp2[p2.length - 1 - i];
    }
	
	public void resetRight(){
		for(int i = 0; i < 8; i++){
			p1[7 - i].setLoc(i, 0);
			p2[7 - i].setLoc(i, 7);
		}
	}

}
