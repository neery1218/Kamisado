package com.radiance.kamisado;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class Piece {//object that conducts piece logic and is used by board
	
	private int locX = 0, locY = 0, rank = 0, color = -1, distance;
    private int owner = 0;
    private int[] playerColor = {Color.parseColor("#090404"), Color.parseColor("#ffecf0f1")};
    private double[] x = {0, 1, 1, 0, -1, -1};
    private double[] y = {1, 0.7, -0.7, -1, -0.7, 0.7};
    private float outerEdge = 0.9f; //space between outer and inner edge is the player color piece
    private float innerEdge = 0.7f;

    public Piece(Piece p){
        locX = p.getX();
        locY = p.getY();
        rank = p.getRank();
        color = p.getColor();
        owner = p.getOwner();
    }

    public Piece(int x, int y, int color, int rank){
        this.rank = rank;
		locX = x;
		locY = y;
        this.color = color;
        distance = 7 - 2 * rank;
	}

    public Piece(int color, int owner) {
        this.color = color;
        this.owner = owner;
        rank = 0;
        distance = 7;
    }

    public Piece(int color, int owner, int rank) {
        this.color = color;
        this.owner = owner;
        this.rank = rank;
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

    public int getOwner() {
        return owner;
    }

    public Point getPoint(){
        return new Point(locX, locY);
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

    public void draw(Canvas canvas, Paint paint, float startX, float startY, float unitSize, int PLAYER_TWO, int PLAYER_ONE){
        paint.setColor(playerColor[this.getOwner()]);//put in array
        canvas.drawCircle(startX + locX * unitSize + unitSize / 2, startY + unitSize * locY + unitSize / 2, (unitSize / 2) * outerEdge, paint);
        paint.setColor(this.getColor());
        canvas.drawCircle(startX + locX * unitSize + unitSize / 2, startY + unitSize * locY + unitSize / 2, (unitSize / 2) * innerEdge, paint);
    }

}
