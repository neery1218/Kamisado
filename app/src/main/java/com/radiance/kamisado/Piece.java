package com.radiance.kamisado;

import android.util.Log;

public class Piece {
	
	private int locX = 0, locY = 0, rank = 0, color = -1, distance;

	public Piece(int x, int y, int color, int rank){
        this.rank = rank;
		locX = x;
		locY = y;
        this.color = color;
        distance = 7 - 2 * rank;
	}
	
	public int getX(){
		return locX;
	}

    public void setX(int x) {
        locX = x;
    }

    public int getY() {
        return locY;
    }

    public void setY(int y) {
        locY = y;
	}

    public int getRank() {
        return rank;
    }

    public void setLoc(int x, int y) {
        locX = x;
        locY = y;
	}

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getDistance(){return distance;}

    public void rankUp (){
        rank++;
        distance-=2;
        Log.v("GAT", "Rankup");
    }
    public String toString(){return "Piece X=" + locX + " Y=" + locY + " Rank=" + rank + " Color=" + color;}

}
