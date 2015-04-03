package com.radiance.kamisado;

public class Board {
	int[][] boardColor, boardPiece;
	Piece[] p1, p2;
	
	public Board(GameBoard m){
	}
	
	public void resetLeft(){
		for(int i = 0; i < 8; i++){
			p1[i].setLoc(i, 0);
			boardPiece[0][i] = 1;
			boardPiece[7][i] = 2;
			p2[i].setLoc(i, 7);
		}
	}
	
	public void resetRight(){
		for(int i = 0; i < 8; i++){
			p1[7 - i].setLoc(i, 0);
			boardPiece[0][i] = 1;
			boardPiece[7][i] = 2;
			p2[7 - i].setLoc(i, 7);
		}
	}

}
