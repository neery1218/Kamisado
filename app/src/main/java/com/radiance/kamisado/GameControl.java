package com.radiance.kamisado;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

public class GameControl implements GameBoardView.OnBoardEvent {//runs the game counter and controls gameBoardView calls
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;
    Board board = new Board();
    private boolean firstMove = true;
    private boolean scoreLimitReached = false;
    private Point inValid = new Point(-1, -1);
    private Player[] players;
    private GameBoardView gameBoardView;
    private int[] scores = {1, 3, 7, 15, 0};
    private int boardDimension = 8;
    private int counter = 1;
    private int[] score = new int[2];
    private int currColor = -1;
    private Piece selectedPiece;
    private ArrayList<Point> availMoves;
    private int sumoChain = 0;
    private int AI_DIFFICULTY = 0;
    private int MATCH_TYPE;
    private Piece init;
    private Piece fin;
    private Piece winPiece;
    private Point win = new Point(-1, -1);
    private int deadlockCount = 0;
    private boolean aiWin = false;
    private boolean deadlock = false;

    private Board resetBoard = null;

    private Stack<MoveGroup> moveStack;
    private int undoCount;
    private Handler handler;

    private GameStateListener gameStateListener;


    public GameControl(GameBoardView gameBoardView, int bd, int VERSUS_TYPE, int MATCH_TYPE) {
        this.boardDimension = bd;
        this.gameBoardView = gameBoardView;
        this.MATCH_TYPE = MATCH_TYPE;
        handler = new Handler();
        players = new Player[2];
        board = new Board();
        players[PLAYER_TWO] = new HumanPlayer(PLAYER_TWO);
        //winPiece = new Piece(null);
        switch (VERSUS_TYPE) {
            case MainActivity.TWO_PLAY_PRESSED:
                players[PLAYER_ONE] = new HumanPlayer(PLAYER_ONE);
                Log.v("Game", "HumanPlayer");
                break;
            case MainActivity.PLAY_PRESSED:
                AI_DIFFICULTY = GamePlayFragment.getAiDifficulty();
                Log.v("Game", "" + AI_DIFFICULTY);
                players[PLAYER_ONE] = new AIPlayer(AI_DIFFICULTY, PLAYER_ONE);
                Log.v("Game", "AIPlayer");
                break;
        }

        currColor = board.getColor(boardDimension - 1, 0);
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);

        selectedPiece = null;
        availMoves = new ArrayList<>();
        firstMove = true;
        counter = 1;

        undoCount = 0;
        moveStack = new Stack<>();


        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board, selectedPiece, false);

    }


    public void resolveSumoPushP1() {//moves the pieces from a player one sumopush
        //find pieces that are gonna get sumo pushed
        MoveGroup sumoMove = new MoveGroup();
        for (int j = sumoChain; j >= 1; j--) {
            sumoMove.add(new Move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX())));
        }
        //board.move(new Point(selectedPiece.getY() - j, selectedPiece.getX()), new Point(selectedPiece.getY() - j - 1, selectedPiece.getX()));

        sumoMove.add(new Move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX())));
        // board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() - 1, selectedPiece.getX()));
        board.move(sumoMove);
        moveStack.push(sumoMove);
    }

    public void resolveSumoPushP2() {//moves the pieces from a player two sumopush
        MoveGroup sumoMove = new MoveGroup();
        //Pushing from the other end so it doesn't get overwritten
        for (int j = sumoChain; j >= 1; j--) {
            sumoMove.add(new Move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX())));
        }
        // board.move(new Point(selectedPiece.getY() + j, selectedPiece.getX()), new Point(selectedPiece.getY() + j + 1, selectedPiece.getX()));


        //board.move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX()));
        sumoMove.add(new Move(new Point(selectedPiece.getY(), selectedPiece.getX()), new Point(selectedPiece.getY() + 1, selectedPiece.getX())));
        board.move(sumoMove);
        moveStack.push(sumoMove);
    }

    public boolean resolveFirstMove(int x, int y) {//used to display moves when it's the first move of a game
        if (!board.getTile(y, x).isEmpty() && board.getTile(y, x).getPiece().getOwner() == counter % 2) {
            selectedPiece = board.getTile(y, x).getPiece();
            availMoves = players[counter % 2].calcMoves(board, selectedPiece);
            gameBoardView.setSelectedPiece(selectedPiece);
            gameBoardView.setAvailMoves(availMoves);
            gameBoardView.drawBoard(board, selectedPiece, false);
            return false;
        } else if (selectedPiece == null)
            return false;
        return true;
    }

    public void resolveNormalMove(int x, int y, int sumoPush) {//finds the next piece and availMoves. also checks for no moves and/or deadlock
        currColor = board.getColor(y, x);
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);
        Log.v("Game", "availMoves: " + availMoves.size());
        if (availMoves.size() == 0 && win.equals(-1, -1)) {//if there are no available moves, it skips the player's turn
            deadlockCount++;
            if (deadlockCount == 2) {//this means that both players can't move
                win = new Point(selectedPiece.getY(), selectedPiece.getX());
                deadlock = true;
                //new rules: if deadlock, it's a tie
                gameStateListener.deadlock(win);
                //TODO: make deadlock screen

            } else {
                counter++;
                resolveNormalMove(selectedPiece.getX(), selectedPiece.getY(), 1);
            }
        } else {

            deadlockCount = 0;
        }
        if (!win.equals(-1, -1)) {
            availMoves = new ArrayList<>();
        }
    }

    public void resolveAiWin(){
        aiWin = false;
        onSwipeLeft();
        Point A = players[counter % 2].selectPiece(board);
        selectedPiece = board.getTile(A.x, A.y).getPiece();
        gameBoardView.setSelectedPiece(selectedPiece);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);
    }

    public Point getWin() {
        return win;
    }//getter method used by gameBoardView

    public boolean aiWin() {
        return aiWin;
    }

    @Override
    public void onTouch(int x, int y) {//overriden method from the interface: all method calls originate from here
        if(scoreLimitReached)return;
        if(gameBoardView.animationRunning){return;}

        if (aiWin) {
            resolveAiWin();
        }

        if (firstMove) {//first move has its own resolve method
            Log.d("test", x + " " + y);
            if(x == -1 || y == -1)
                return;
            if(players[counter % 2] instanceof HumanPlayer && !resolveFirstMove(x, y))
                return;
        }
        firstMove = false;
        Log.d("test", "" + firstMove);


        Point temp = players[counter % 2].resolveMove(new Point(y, x));//returns the point that the piece should be moved to
        if (!temp.equals(inValid)) {//check validity
            if (selectedPiece.getRank() > 0 && temp.equals(players[counter % 2].getSumoPushPoint())) {//if it's sumo:
                sumoPush(temp);
            } else {
                movePiece(temp);
            }

            counter++;
            resolveNormalMove(temp.y, temp.x, 0);
            gameBoardView.setAvailMoves(availMoves);
            gameBoardView.drawBoard(board, init.getPoint(), fin.getPoint(), selectedPiece);

            //find next piece
            if(!deadlock) {
                win = GameLogic.win(board);
                if (!win.equals(-1, -1)) {//if someone won:
                    resolveWin();
                }
            }

            if (!win.equals(-1, -1)) {
                counter = board.getTile(win.x, win.y).getPiece().getOwner();
                if (players[counter % 2] instanceof AIPlayer) {
                    aiWin = true;
                }
            } else {
                if (players[counter % 2] instanceof AIPlayer && win.equals(-1, -1))
                    onTouch(-1, -1);
            }
        }
    }

    public void sumoPush(Point temp){
        init = new Piece(board.getTile(selectedPiece.getY(), selectedPiece.getX()).getPiece());
        sumoChain = players[counter % 2].getSumoChain();
        if (undoCount > 0)
            undoCount--;
        switch (counter % 2) {
            case PLAYER_TWO:
                resolveSumoPushP1();
                break;
            case PLAYER_ONE:
                resolveSumoPushP2();
                break;
        }
        counter++;
        fin = new Piece(board.getTile(temp.x, temp.y).getPiece());
    }

    public void movePiece(Point temp){
        init = new Piece(board.getTile(selectedPiece.getY(), selectedPiece.getX()).getPiece());
        Move move = new Move(new Point(selectedPiece.getY(), selectedPiece.getX()), temp);
        moveStack.push(new MoveGroup(move));
        board.move(move);
        if (undoCount > 0)
            undoCount--;
        fin = new Piece(board.getTile(temp.x, temp.y).getPiece());
    }

    public void resolveWin(){
        winPiece = board.getTile(win.x, win.y).getPiece();
        int winPlayer = winPiece.getOwner();
        /*if(winPlayer == PLAYER_ONE){
            gameStateListener.p2Win(winPiece.getPoint());
        }
        else{
            gameStateListener.p1Win(winPiece.getPoint());
        }*/
        new CallWinTask().execute(winPiece, new Piece(-1, -1));
        score[winPlayer] += scores[winPiece.getRank()];
        Log.d("GAMESTATE", score[winPlayer] + " " + MATCH_TYPE);

        gameBoardView.updateScore(score);
        board.rankUp(winPiece.getY(), winPiece.getX());
        Log.d("TEST", "win");
    }

    public void reset() {//resets the game board
        if(!deadlock)
            counter = (board.getTile(win.x, win.y).getPiece().getOwner() + 1) % 2;
        win = new Point(-1, -1);
        deadlock = false;
        deadlockCount = 0;
        firstMove = true;
        selectedPiece = null;
        gameBoardView.setSelectedPiece(null);
        gameBoardView.setResetBoard(resetBoard);
        gameBoardView.drawBoard(board, selectedPiece, true);
        undoCount = 0;
        moveStack = new Stack<>();
    }

    public void undo() {

        if (moveStack.isEmpty()) {
            firstMove = true;
            return;
        }
        undoCount++;
        MoveGroup undo = moveStack.pop().reverse();

        if (undo.size() == 1)
            counter = (counter + 1) % 2;

        //resolveNormalMove(undo.finish.y, undo.finish.x);
        init = new Piece(board.getTile(undo.get(0).start.x, undo.get(0).start.y).getPiece());
        board.move(undo);
        currColor = board.getTile(undo.get(0).finish.x, undo.get(0).finish.y).getPiece().getColor();
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);
        availMoves = players[counter % 2].calcMoves(board, selectedPiece);

        fin = new Piece(board.getTile(undo.get(0).finish.x, undo.get(0).finish.y).getPiece());
        gameBoardView.setAvailMoves(availMoves);
        gameBoardView.drawBoard(board, init.getPoint(), fin.getPoint(), selectedPiece);
        if (moveStack.isEmpty()) {
            firstMove = true;
        }

        Log.v("Game", "undo");
    }

    public void attachGamePlayFragment(GamePlayFragment gamePlayFragment){
        gameStateListener = gamePlayFragment;
    }

    public void callWin(int player, Point point){
        if (score[player] >= MATCH_TYPE) {
            gameStateListener.gameLimitReached(player);
            scoreLimitReached = true;
        } else if (player == PLAYER_ONE)
            gameStateListener.p2Win(point);
        else
            gameStateListener.p1Win(point);
    }

    @Override
    public void onSwipeRight() {
        resetBoard = new Board(board);
        board.search();
        board.fillLeft();
        reset();
    }

    @Override
    public void onSwipeLeft() {
        resetBoard = new Board(board);
        board.search();
        board.fillLeft();
        reset();
    }

    public interface GameStateListener{
        public void p1Win(Point winPoint);
        public void p2Win(Point winPoint);
        public void deadlock(Point winPoint);
        public void gameLimitReached(int player);
    }

    private class CallWinTask extends AsyncTask<Piece, Integer, Boolean> {
        protected Boolean doInBackground(Piece... pieces) {
            Log.d("TASK", "called");
            while(true)
                if(!gameBoardView.animationRunning){
                    if (pieces[1].getPoint().x != -1 && pieces[1].getPoint().y != -1) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                callWin(winPiece.getOwner(), winPiece.getPoint());

                            }
                        });
                    }

                    else if (pieces[1].getPoint().x == 0 && pieces[1].getPoint().y == 0) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                gameStateListener.deadlock(winPiece.getPoint());

                            }
                        });

                    }

                    else
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                gameStateListener.gameLimitReached(winPiece.getOwner());

                            }
                        });

                    break;
                }
            return false;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Boolean result) {
        }
    }
}
