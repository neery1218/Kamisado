package com.radiance.kamisado;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MatchLengthFragment extends Fragment {

    private OnMatchLengthInteraction mListener;

    private Button singleButton;
    private Button standardButton;
    private Button extendedButton;
    private Button marathonButton;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchLengthFragment.
     */
    public static MatchLengthFragment newInstance(String param1, String param2) {
        MatchLengthFragment fragment = new MatchLengthFragment();
        return fragment;
    }

    public MatchLengthFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_match_length, container, false);

        singleButton = (Button)view.findViewById(R.id.singleButton);
        singleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onMatchLengthInteraction(MainActivity.MATCH_SINGLE);
            }
        });

        standardButton = (Button)view.findViewById(R.id.standardButton);
        standardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onMatchLengthInteraction(MainActivity.MATCH_STANDARD);
            }
        });

        extendedButton = (Button)view.findViewById(R.id.extendedButton);
        extendedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onMatchLengthInteraction(MainActivity.MATCH_EXTENDED);
            }
        });

        marathonButton = (Button)view.findViewById(R.id.marathonButton);
        marathonButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onMatchLengthInteraction(MainActivity.MATCH_MARATHON);
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMatchLengthInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMatchLengthInteraction");
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
    public interface OnMatchLengthInteraction {
        // TODO: Update argument type and name
        public void onMatchLengthInteraction(int button);
    }

}
