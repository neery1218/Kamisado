package com.radiance.kamisado;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GamePlayFragment extends Fragment {

    private static int VERSUS_TYPE;
    private static int MATCH_TYPE;
    private GameBoardView gameBoardView;

    private OnGamePlayInteractionListener mListener;

    private TextView scoreTextView;


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




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            VERSUS_TYPE = getArguments().getInt(MainActivity.ARG_VERSUS_TYPE);
            MATCH_TYPE = getArguments().getInt(MainActivity.ARG_MATCH_TYPE);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);

        scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);
        scoreTextView.setText("yo");

        gameBoardView = (GameBoardView) view.findViewById(R.id.gameBoard);
        gameBoardView.setScoreView(scoreTextView);

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


    public interface OnGamePlayInteractionListener {
        // TODO: Update argument type and name
        public void onGamePlayInteraction(Uri uri);
    }

}
