package com.radiance.kamisado;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class IntroFragment extends Fragment {

    private OnIntroInteractionListener mListener;
    private Button playButton;
    private Button tutorialButton;
    private Button playerTwoButton;
    private Button onlineButton;

    private TextView titleTextView;
    private introBoardView introBoardView;

    public IntroFragment() {
        // Required empty public constructor
    }

    public

    static IntroFragment newInstance(String param1, String param2) {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    @
            Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        titleTextView.setTypeface(MainActivity.typefaceHeader);
        titleTextView.setTextSize(64f);

        introBoardView = (introBoardView) view.findViewById(R.id.introBoardView);
        introBoardView.setRotation(30f);
        introBoardView.invalidate();

        playButton = (Button)view.findViewById(R.id.playButton);
        playButton.setTypeface(MainActivity.typefaceHeader);
        //playButton.getBackground().setColorFilter(R.color.white, PorterDuff.Mode.MULTIPLY);
        //playButton.setAlpha(1f);


        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onIntroInteraction(MainActivity.PLAY_PRESSED);
            }
        });
        playButton.setTextSize(24f);

        playerTwoButton = (Button)view.findViewById(R.id.playerTwoButton);
        playerTwoButton.setTextSize(24f);
        playerTwoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onIntroInteraction(MainActivity.TWO_PLAY_PRESSED);
            }
        });
        playerTwoButton.setTypeface(MainActivity.typefaceHeader);
        onlineButton = (Button)view.findViewById(R.id.onlineButton);
        onlineButton.setTypeface(MainActivity.typefaceHeader);
        playButton.setTextSize(24f);
        onlineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onIntroInteraction(MainActivity.ONLINE_PLAY_PRESSED);
            }
        });

        tutorialButton = (Button)view.findViewById(R.id.tutorialButton);
        tutorialButton.setTypeface(MainActivity.typefaceHeader);
        tutorialButton.setTextSize(24f);
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onIntroInteraction(MainActivity.TUTORIAL_PRESSED);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnIntroInteractionListener) activity;
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


    public interface OnIntroInteractionListener {
        // TODO: Update argument type and name
        public void onIntroInteraction(int button);
    }

}
