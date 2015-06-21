package com.radiance.kamisado;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class Piece {//object that conducts piece logic and is used by board
	
	private int locX = 0, locY = 0, rank = 0, color = -1, distance;
    private int owner = 0;
    private int[] playerColor = {Color.parseColor("#090404"), Color.parseColor("#ffecf0f1")};

    private double[] x = {0, 1, 1, 0, -1, -1};
    private double[] y = {1, 0.7, -0.7, -1, -0.7, 0.7};
    private float outerEdge = 0.9f; //space between outer and inner edge is the player color piece
    private float innerEdge = 0.7f;

    private float rankEdge = 0.3f;
    private float[] rankX = {-0.7071067f, 0, 0.7071067f, 0};
    private float[] rankY = {-0.7071067f, 0.7f, -0.7071067f, 0};


    public Piece(Piece p){
        locX = p.getX();
        locY = p.getY();
        rank = p.getRank();
        color = p.getColor();
        owner = p.getOwner();
    }

    public Piece(int x, int y, int color, int rank, int owner){
        this.rank = rank;
		locX = x;
		locY = y;
        this.color = color;
        this.owner = owner;
        // distance = 7 - 2 * rank;
        distance = 7;
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
        //distance = 7 - 2 * rank;
        distance = 7;
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
        //distance-=2;
    }

    public String toString(){return "Piece X=" + locX + " Y=" + locY + " Rank=" + rank + " Color=" + color;}

    public void draw(Canvas canvas, Paint paint, float startX, float startY, float unitSize, int PLAYER_TWO, int PLAYER_ONE, int animateAlpha){
        paint.setColor(Color.argb(animateAlpha, Color.red(playerColor[this.getOwner()]), Color.green(playerColor[this.getOwner()]), Color.blue(playerColor[this.getOwner()])));//put in array
        canvas.drawCircle(startX + locX * unitSize + unitSize / 2, startY + unitSize * locY + unitSize / 2, (unitSize / 2) * outerEdge, paint);
        paint.setColor(Color.argb(animateAlpha, Color.red(this.getColor()), Color.green(this.getColor()), Color.blue(this.getColor())));
        canvas.drawCircle(startX + locX * unitSize + unitSize / 2, startY + unitSize * locY + unitSize / 2, (unitSize / 2) * innerEdge, paint);
        drawRank(canvas, startX, startY, unitSize, animateAlpha);
    }

    private void drawRank(Canvas canvas, float startX, float startY, float unitSize, int alpha) {
        Paint playerPaint = new Paint();
        playerPaint.setColor(playerColor[getOwner()]);
        playerPaint.setAlpha(alpha);
        playerPaint.setStyle(Paint.Style.FILL);

        Paint piecePaint = new Paint();
        piecePaint.setColor(this.getColor());
        piecePaint.setAlpha(alpha);
        piecePaint.setStyle(Paint.Style.FILL);

        Path outerPath = new Path();
        Path innerPath = new Path();
        //find center
        double xCenter = startX + locX * unitSize + (unitSize / 2), yCenter = startY + locY
                * unitSize + (unitSize / 2);

        outerPath.reset(); // only needed when reusing this path for a new build
        innerPath.reset();
        double radius = unitSize / 2;
        outerPath.moveTo(Math.round(xCenter + x[0] * outerEdge * radius), Math.round(yCenter + y[0] * outerEdge * radius)); // used for first point
        innerPath.moveTo(Math.round(xCenter + x[0] * innerEdge * radius), Math.round(yCenter + y[0] * innerEdge * radius));
        for (int i = 1; i < x.length; i++) {
            outerPath.lineTo(Math.round(xCenter + x[i] * outerEdge * radius), Math.round(yCenter + y[i] * outerEdge * radius));
            innerPath.lineTo(Math.round(xCenter + x[i] * innerEdge * radius), Math.round(yCenter + y[i] * innerEdge * radius));
        }
        //  playerPaint.setColor(Color.BLACK);
        playerPaint.setAntiAlias(true);
        piecePaint.setAntiAlias(true);
        /*canvas.drawPath(outerPath, playerPaint);
        canvas.drawPath(innerPath, piecePaint);*/
        /*for (int i = 0; i < rank; i++) {
            canvas.drawCircle((float) xCenter + rankEdge * (float)radius * rankX[i], (float) yCenter + rankEdge * (float)radius* rankY[i], unitSize / 2 * rankEdge, playerPaint);
        }*/
        float avgEdge = (innerEdge + outerEdge) / 2;
        float widthEdge = 0.1f;
        if (rank > 0) {
            Path path = new Path();
            float topCenterX = (float) (xCenter - avgEdge * radius * rankX[2]);
            float topCenterY = (float) (yCenter - avgEdge * radius * rankX[2]);
            float bottomCenterX = (float) (xCenter + avgEdge * radius * rankX[2]);
            float bottomCenterY = (float) (yCenter + avgEdge * radius * rankX[2]);


            path.moveTo(Math.round(topCenterX + widthEdge * radius * rankX[2]), Math.round(topCenterY - widthEdge * radius * rankX[2]));
            path.lineTo(Math.round(topCenterX - widthEdge * radius * rankX[2]), Math.round(topCenterY + widthEdge * radius * rankX[2]));
            path.lineTo(Math.round(bottomCenterX + widthEdge * radius * rankX[2]), Math.round(bottomCenterY - widthEdge * radius * rankX[2]));
            path.lineTo(Math.round(bottomCenterX - widthEdge * radius * rankX[2]), Math.round(bottomCenterY + widthEdge * radius * rankX[2]));

            canvas.drawPath(path, playerPaint);
            if (rank > 1) {

                Path doublePath = new Path();
                doublePath.moveTo(Math.round(bottomCenterX + widthEdge * radius * rankX[2]), Math.round(topCenterY - widthEdge * radius * rankX[2]));
                doublePath.lineTo(Math.round(bottomCenterX - widthEdge * radius * rankX[2]), Math.round(topCenterY + widthEdge * radius * rankX[2]));
                doublePath.lineTo(Math.round(topCenterX + widthEdge * radius * rankX[2]), Math.round(bottomCenterY - widthEdge * radius * rankX[2]));
                doublePath.lineTo(Math.round(topCenterX - widthEdge * radius * rankX[2]), Math.round(bottomCenterY + widthEdge * radius * rankX[2]));
                canvas.drawPath(doublePath, playerPaint);
            }
        }
        //draw diagonal line for single sumo, draw x for double sumo, d

    }

}
