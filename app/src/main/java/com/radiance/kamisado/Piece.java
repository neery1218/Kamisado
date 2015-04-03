package com.radiance.kamisado;
import android.graphics.Color;

public class Piece {
	
	private int locX = 0, locY = 0, upgrade = 0;
	private CurColor color;
	
	public enum Rank {
		REGULAR,SUMO, DOUBLESUMO, TRIPSUMO, QUADSUMO
	}
	public enum CurColor{
		RED, PINK, BLUE, BROWN, YELLOW, GREEN, ORANGE, PURPLE
	}
	public enum BoardColor{
		RED, PINK, BLUE, BROWN, YELLOW, GREEN, ORANGE, PURPLE
	}
	public Piece(int x, int y, CurColor c){
		locX = x;
		locY = y;
		color = c;
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
	
	public int getColor(){
		switch(color){
		case RED: return Color.RED;
		case BLUE: return Color.BLUE;
		case ORANGE: return Color.parseColor("#ED872D");
		case GREEN: return Color.GREEN;
		case YELLOW: return Color.YELLOW;
		case PINK: return Color.parseColor("#FFB7C5");
		case PURPLE: return Color.parseColor("#69359C");
		case BROWN: return Color.parseColor("#964B00");
		}
		return 0;
	}

}
