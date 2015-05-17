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

public class GamePlayFragment extends Fragment implements Button.OnClickListener {

    private static int VERSUS_TYPE;
    private static int MATCH_TYPE;
    private static int AI_DIFFICULTY;
    private GameBoardView gameBoardView;

    private TextView[] scoreTextViews;

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
        scoreTextViews = new TextView[2];

        scoreTextViews[0] = new TextView(getActivity());
        scoreTextViews[1] = new TextView(getActivity());
        if (getArguments() != null) {
            VERSUS_TYPE = getArguments().getInt(MainActivity.ARG_VERSUS_TYPE);
            MATCH_TYPE = getArguments().getInt(MainActivity.ARG_MATCH_TYPE);
            if (VERSUS_TYPE == MainActivity.PLAY_PRESSED)
                AI_DIFFICULTY = getArguments().getInt(MainActivity.ARG_AI_DIFFICULTY);


        }
    }

    private void setupUserBar(int player) {
        userLayouts[player].removeAllViews();


        //scoreTextView.setLayoutParams(params);

        UndoButton undoButton = new UndoButton(getActivity(), player);
        undoButton.setText("Undo");
        undoButton.setOnClickListener(this);
        userLayouts[player].addView(scoreTextViews[player]);
        userLayouts[player].addView(undoButton);
        //undoButton.setLayoutParams(params); i haven't computed them yet

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

            setupUserBar(GameControl.PLAYER_ONE);

        } else if (VERSUS_TYPE == MainActivity.TWO_PLAY_PRESSED) {//vs two player
            //both layouts are the same, but rotated
            //add onclick listener to call the undoPressed() method
            setupUserBar(GameControl.PLAYER_TWO);
            userLayouts[GameControl.PLAYER_TWO].setRotation(180f);
            setupUserBar(GameControl.PLAYER_ONE);
        }

        gameBoardView = (GameBoardView) view.findViewById(R.id.gameBoard);
        //gameBoardView needs to accept two views
        gameBoardView.setScoreView(scoreTextViews);
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

    @Override
    public void onClick(View v) {
        UndoButton button = (UndoButton) v;
        gameBoardView.undo(button.getPlayer());
    }


    public interface OnGamePlayInteractionListener {
        // TODO: Update argument type and name
        public void onGamePlayInteraction(Uri uri);
    }

}
