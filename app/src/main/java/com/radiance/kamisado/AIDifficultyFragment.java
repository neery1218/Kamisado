package com.radiance.kamisado;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class AIDifficultyFragment extends Fragment {


    private OnDifficultyInteraction mListener;

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
        return inflater.inflate(R.layout.fragment_aidifficulty, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int level) {
        if (mListener != null) {
            mListener.onDifficultyInteraction(level);
        }
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
