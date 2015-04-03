package com.radiance.kamisado;
import android.graphics.Color;

public class Piece {
	
	private int locX = 0, locY = 0, upgrade = 0, color = -1;

	public Piece(int x, int y, int color){
		locX = x;
		locY = y;
        this.color = color;
	}
	
	public int getX(){
		return locX;
	}
	
	public int getY(){
		return locY;
	}
	
	public void setLoc(int x, int y){
		locX = x;
		locY = y;
	}
	
	public void setX(int x){
		locX = x;
	}
	
	public void setY(int y){
		locY = y;
	}

    public void setColor(int color){this.color = color;}

    public int getColor(){return color;}

}
