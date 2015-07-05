package com.radianceTOPS.constain;

import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;

public class GameControl implements GameBoardView.OnBoardEvent {//runs the game counter and controls gameBoardView calls
    public static final int PLAYER_ONE = 0;
    public static final int PLAYER_TWO = 1;
    private static int VERSUS_TYPE = 0;
    Board board = new Board();
    private boolean firstMove = true;
    private boolean scoreLimitReached = false;
    private Point inValid = new Point(-1, -1);
    private Player[] players;
    private GameBoardView gameBoardView;
    private int[] scores = {1, 1, 7, 1, 0};
    private int boardDimension = 8;
    private int counter = 1;
    private int[] score = new int[2];
    private int currColor = -1;
    private Piece selectedPiece;
    private ArrayList<Point> availMoves;
    private int sumoChain = 0;
    private int AI_DIFFICULTY = 0;
    private int MATCH_TYPE;
    private int undoLimit;
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
        this.VERSUS_TYPE = VERSUS_TYPE;
        this.boardDimension = bd;
        this.gameBoardView = gameBoardView;
        this.MATCH_TYPE = MATCH_TYPE;
        undoLimit = MATCH_TYPE * 2 / 3 - 100;
        handler = new Handler();
        players = new Player[2];
        board = new Board();
        players[PLAYER_TWO] = new HumanPlayer(PLAYER_TWO);
        //winPiece = new Piece(null);
        switch (VERSUS_TYPE) {
            case MainActivity.TWO_PLAY_PRESSED:
                players[PLAYER_ONE] = new HumanPlayer(PLAYER_ONE);
                break;
            case MainActivity.PLAY_PRESSED:
                AI_DIFFICULTY = GamePlayFragment.getAiDifficulty();
                players[PLAYER_ONE] = new AIPlayer(AI_DIFFICULTY, PLAYER_ONE);
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
        MoveGroup sumoMove = new MoveGroup(counter);
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
        MoveGroup sumoMove = new MoveGroup(counter);
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
            availMoves = players[counter % 2].calcMoves(board, counter % 2, selectedPiece);
            players[counter % 2].setAvailMoves(availMoves);
            players[counter % 2].setBoard(board);
            players[counter % 2].setSelectedPiece(selectedPiece);
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
        availMoves = players[counter % 2].calcMoves(board, counter % 2, selectedPiece);
        players[counter % 2].setAvailMoves(availMoves);
        players[counter % 2].setBoard(board);
        players[counter % 2].setSelectedPiece(selectedPiece);
        if (availMoves.size() == 0 && win.equals(-1, -1)) {//if there are no available moves, it skips the player's turn
            deadlockCount++;
            if (deadlockCount == 2) {//this means that both players can't move
                //win = new Point(selectedPiece.getY(), selectedPiece.getX());
                deadlock = true;
                win = new Point(-2, -2);
                //new rules: if deadlock, it's a tie
                // gameStateListener.deadlock(win);
                //winPiece = board.getTile(win.x, win.y).getPiece();
                winPiece = selectedPiece;
                new CallWinTask().execute(winPiece, new Piece(-1, -1));

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

        // onSwipeLeft();

    }

    public Point getWin() {
        return win;
    }//getter method used by gameBoardView

    public boolean aiWin() {
        return aiWin;
    }

    public boolean getFirstMove() {
        return firstMove;
    }
    @Override
    public void onTouch(int x, int y) {//overriden method from the interface: all method calls originate from here
        if (scoreLimitReached || !win.equals(-1, -1)) return; //game's over
        if (gameBoardView.animationRunning) {
            return;
        } //if animation's running, the onTouch is irrelevant

        if (aiWin) {
            resolveAiWin();
        }

        if (firstMove) {//first move has its own resolve method
            if(x == -1 || y == -1)
                return;
            if(players[counter % 2] instanceof HumanPlayer && !resolveFirstMove(x, y))
                return;
        }
        firstMove = false;

        Point temp = players[counter % 2].resolveMove(new Point(y, x));//returns the point that the piece should be moved to
        if (!temp.equals(inValid)) {//check validity

            movePiece(temp);


            win = GameLogic.win(board);

            if (!win.equals(-1, -1)) {//if someone won:
                resolveWin();
            }

            counter++;
            resolveNormalMove(temp.y, temp.x, 0);

            if (players[counter % 2] instanceof HumanPlayer)
                gameBoardView.setAvailMoves(availMoves);
            else
                gameBoardView.setAvailMoves(new ArrayList<Point>());
            gameBoardView.drawBoard(board, init.getPoint(), fin.getPoint(), selectedPiece);

            if (players[counter % 2] instanceof AIPlayer && win.equals(-1, -1)) {
                onTouch(-1, -1);
            }


            //find next piece

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
        moveStack.push(new MoveGroup(move, counter));
        board.move(move);
        if (undoCount > 0)
            undoCount--;
        fin = new Piece(board.getTile(temp.x, temp.y).getPiece());
        Point openings = GameLogic.findOpenings(board);
    }

    public void resolveWin(){
        winPiece = board.getTile(win.x, win.y).getPiece();
        int winPlayer = winPiece.getOwner();

        new CallWinTask().execute(winPiece, new Piece(-1, -1));
        score[winPlayer] += scores[winPiece.getRank()];

        gameBoardView.updateScore(score);
        board.rankUp(winPiece.getY(), winPiece.getX());
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
        undoLimit = MATCH_TYPE * 2 / 3 - 100;
        moveStack = new Stack<>();

        if (players[(counter) % 2] instanceof AIPlayer) {
            Point A = players[counter % 2].selectPiece(board);
            selectedPiece = board.getTile(A.x, A.y).getPiece();
            gameBoardView.setSelectedPiece(selectedPiece);
            availMoves = players[counter % 2].calcMoves(board, counter % 2, selectedPiece);
            players[counter % 2].setAvailMoves(availMoves);
            players[counter % 2].setBoard(board);
            players[counter % 2].setSelectedPiece(selectedPiece);
            firstMove = false;
            onTouch(-1, -1);
        }

    }

    public void undo() {
        if(VERSUS_TYPE == MainActivity.PLAY_PRESSED && undoLimit == 0){
            gameBoardView.showUndoLimitToast();
            return;
        }
        undoLimit--;

        if (moveStack.isEmpty()) {
            firstMove = true;
            return;
        }
        undoCount++;
        MoveGroup undo = moveStack.pop().reverse();


        counter = undo.getCounter();



        /*if (undo.size() == 1)
            counter = (counter + 1) % 2;*/

        //resolveNormalMove(undo.finish.y, undo.finish.x);
        init = new Piece(board.getTile(undo.get(0).start.x, undo.get(0).start.y).getPiece());
        board.move(undo);
        currColor = board.getTile(undo.get(0).finish.x, undo.get(0).finish.y).getPiece().getColor();
        selectedPiece = GameLogic.findPiece(board, counter % 2, currColor);
        availMoves = players[counter % 2].calcMoves(board, counter % 2, selectedPiece);
        players[counter % 2].setAvailMoves(availMoves);
        players[counter % 2].setBoard(board);
        players[counter % 2].setSelectedPiece(selectedPiece);

        fin = new Piece(board.getTile(undo.get(0).finish.x, undo.get(0).finish.y).getPiece());
        gameBoardView.setAvailMoves(availMoves);
        if(!(players[counter % 2] instanceof  AIPlayer))
        if(!firstMove)
            gameBoardView.drawBoard(board, init.getPoint(), fin.getPoint(), selectedPiece);
        else {
            gameBoardView.drawBoard(board, selectedPiece, false);
        }

        if (!moveStack.isEmpty() && (players[counter % 2] instanceof AIPlayer || (!moveStack.isEmpty() && players[moveStack.peek().getCounter() % 2] instanceof HumanPlayer))) {
            if (VERSUS_TYPE == MainActivity.PLAY_PRESSED) {
                undoLimit++;
                undo();
            }
        }
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
            while(true)
                if(!gameBoardView.animationRunning){
                    if (deadlock) {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                gameStateListener.deadlock(winPiece.getPoint());

                            }
                        }, 00);
                    } else if (pieces[1].getPoint().x != -1 && pieces[1].getPoint().y != -1) {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                callWin(winPiece.getOwner(), winPiece.getPoint());

                            }
                        }, 500);
                    }

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
