package com.radiance.kamisado;

import android.graphics.Color;

public class Board {
	int[][] board8Color, board8Piece;
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
        board8Piece = new int[8][8];
	}
	
	public void resetLeft(){
		for(int i = 0; i < 8; i++){
			p1[i].setLoc(i, 0);
			board8Piece[0][i] = 1;
			board8Piece[7][i] = 2;
			p2[i].setLoc(i, 7);
		}
	}
	
	public void resetRight(){
		for(int i = 0; i < 8; i++){
			p1[7 - i].setLoc(i, 0);
			board8Piece[0][i] = 1;
			board8Piece[7][i] = 2;
			p2[7 - i].setLoc(i, 7);
		}
	}

}
