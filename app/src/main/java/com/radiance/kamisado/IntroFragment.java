package com.radiance.kamisado;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class IntroFragment extends Fragment {

    private OnIntroInteractionListener mListener;
    private Button playButton;

    // TODO: Rename and change types and number of parameters
    public static IntroFragment newInstance(String param1, String param2) {
        IntroFragment fragment = new IntroFragment();
        return fragment;
    }

    public IntroFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onIntroInteraction(MainActivity.PLAY_PRESSED);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_intro, container, false);
        playButton = (Button)view.findViewById(R.id.playButton);
        playButton.setText("Play!");

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
