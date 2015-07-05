package com.radianceTOPS.constain;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AIDifficultyFragment extends Fragment {


    private OnDifficultyInteraction mListener;
    private int EASY = 0;
    private int MEDIUM = 1;
    private int HARD = 2;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    private introBoardView introBoardView;

    public AIDifficultyFragment() {
        // Required empty public constructor
    }

    public static AIDifficultyFragment newInstance(String param1, String param2) {
        AIDifficultyFragment fragment = new AIDifficultyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aidifficulty, container, false);

        introBoardView = (introBoardView) view.findViewById(R.id.introBoardView);
        introBoardView.setRotation(30f);
        introBoardView.invalidate();

        easyButton = (Button) view.findViewById(R.id.easyButton);
        easyButton.setTypeface(MainActivity.typefaceHeader);
        easyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onDifficultyInteraction(EASY);
            }
        });

        mediumButton = (Button) view.findViewById(R.id.mediumButton);
        mediumButton.setTypeface(MainActivity.typefaceHeader);
        mediumButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onDifficultyInteraction(MEDIUM);
            }
        });

        hardButton = (Button) view.findViewById(R.id.hardButton);
        hardButton.setTypeface(MainActivity.typefaceHeader);
        hardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onDifficultyInteraction(HARD);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDifficultyInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDifficultyInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDifficultyInteraction {
        // TODO: Update argument type and name
        public void onDifficultyInteraction(int level);
    }

}
