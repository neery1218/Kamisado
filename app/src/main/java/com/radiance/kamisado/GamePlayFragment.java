package com.radiance.kamisado;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GamePlayFragment extends Fragment {

    private static int VERSUS_TYPE;
    private static int MATCH_TYPE;
    private static int AI_DIFFICULTY;
    private GameBoardView gameBoardView;

    private int PLAYER_ONE_SCORE;
    private int PLAYER_TWO_SCORE;

    private OnGamePlayInteractionListener mListener;



    private TextView titleTextView;

    private LinearLayout topUserLayout;
    private LinearLayout bottomUserLayout;

    private LinearLayout[] userLayouts;

    private int height;
    private int width;



    public GamePlayFragment() {
        // Required empty public constructor

    }

    public static GamePlayFragment newInstance(String param1, String param2) {
        GamePlayFragment fragment = new GamePlayFragment();

        return fragment;
    }

    public static int getVERSUS_TYPE() {
        return VERSUS_TYPE;
    }


    public static int getMATCH_TYPE() {
        return MATCH_TYPE;
    }

    public static int getAiDifficulty() {
        return AI_DIFFICULTY;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLayouts = new LinearLayout[2];

        if (getArguments() != null) {
            VERSUS_TYPE = getArguments().getInt(MainActivity.ARG_VERSUS_TYPE);
            MATCH_TYPE = getArguments().getInt(MainActivity.ARG_MATCH_TYPE);
            if (VERSUS_TYPE == MainActivity.PLAY_PRESSED)
                AI_DIFFICULTY = getArguments().getInt(MainActivity.ARG_AI_DIFFICULTY);


        }
    }

    private void setupUserBar(LinearLayout layout, int player) {
        layout.removeAllViews();

        // scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);
        TextView scoreView = new TextView(getActivity());
        scoreView.setText("yo");

        //scoreTextView.setLayoutParams(params);

        Button undoButton = new Button(getActivity());
        undoButton.setText("Undo");
        undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gameBoardView.undo();
            }
        });
        layout.addView(scoreView);
        layout.addView(undoButton);
        //undoButton.setLayoutParams(params); i haven't computed them yet

    }

    private void setupUndoBar(LinearLayout layout) {
        layout.removeAllViews();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);

        View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);//finds alloted screen size. this will save a lot of time.
        Log.v("UI", "Content: " + content.getWidth() + " " + content.getHeight());

        //player two has top layout, player one has bottom layout
        userLayouts[GameControl.PLAYER_ONE] = (LinearLayout) view.findViewById(R.id.bottomUserLayout);
        userLayouts[GameControl.PLAYER_TWO] = (LinearLayout) view.findViewById(R.id.topUserLayout);


        //compute layout sizes
        height = content.getHeight();
        width = content.getWidth();

        int layoutHeight = (height - width) / 2;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutHeight);
        LinearLayout.LayoutParams gameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);

        if (VERSUS_TYPE == MainActivity.PLAY_PRESSED) {//vs AI
            //set top linearlayout to contain textview
            titleTextView = new TextView(getActivity());
            titleTextView.setText("Kamisado");
            titleTextView.setTypeface(MainActivity.typefaceHeader);
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
            titleTextView.setTextColor(getResources().getColor(R.color.text));
            titleTextView.setGravity(Gravity.CENTER);
            titleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            titleTextView.setBackgroundColor(getResources().getColor(R.color.white));
            userLayouts[GameControl.PLAYER_TWO].addView(titleTextView);

            setupUserBar(userLayouts[GameControl.PLAYER_ONE], PLAYER_ONE_SCORE);

        } else if (VERSUS_TYPE == MainActivity.TWO_PLAY_PRESSED) {//vs two player
            //both layouts are the same, but rotated
            //add onclick listener to call the undoPressed() method
            setupUserBar(userLayouts[GameControl.PLAYER_TWO], GameControl.PLAYER_TWO);
            userLayouts[GameControl.PLAYER_TWO].setRotation(180f);
            setupUserBar(userLayouts[GameControl.PLAYER_ONE], GameControl.PLAYER_ONE);
        }

        gameBoardView = (GameBoardView) view.findViewById(R.id.gameBoard);
        //gameBoardView needs to accept two views
        gameBoardView.setScoreView(scoreTextView);
        gameBoardView.setLayoutParams(gameParams);


        // undoButton.setLayoutParams(params);
        //scoreTextView.setRotation(180f); so other players can view
        //bottomUserLayout.addView(undoButton);
        // topUserLayout.addView(scoreTextView);
        userLayouts[GameControl.PLAYER_ONE].setLayoutParams(layoutParams);
        userLayouts[GameControl.PLAYER_TWO].setLayoutParams(layoutParams);
        // userLayout

        //topUserLayout.setLayoutParams(layoutParams);
        //bottomUserLayout.setLayoutParams(layoutParams);
        //topUserLayout.addView(undoButton);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onGamePlayInteraction(uri);
        }
    }

    private void undoPressed(int player) {
        if (VERSUS_TYPE == MainActivity.TWO_PLAY_PRESSED) {
            //pending sign on player's layout
            //check for confirmation by replacing layout of (player + 1)%2 with a textview and 2 y/n boxes
            //add listener to call gameBoardView.undo() if yes is pressed
            //go back to original view
        } else if (VERSUS_TYPE == MainActivity.PLAY_PRESSED) {
            gameBoardView.undo();
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnGamePlayInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGamePlayInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnGamePlayInteractionListener {
        // TODO: Update argument type and name
        public void onGamePlayInteraction(Uri uri);
    }

}
