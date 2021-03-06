package com.radianceTOPS.constrain;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class TutorialFragment extends Fragment{

    int counter;
    private OnTutorialInteractionListener mListener;
    // private ViewAnimator viewAnimator;
    private ImageView tutorialScreen;
    private Button previousButton;
    private Button nextButton;
    private int[] id;
    private int startX, endX, startY, endY;
    public TutorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TutorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment newInstance(String param1, String param2) {
        TutorialFragment fragment = new TutorialFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        int[] id = {R.drawable.tutorialone, R.drawable.tutorialtwo, R.drawable.tutorialthree, R.drawable.tutorialfour, R.drawable.tutorialfive, R.drawable.tutorialsix, R.drawable.tutorialseven, R.drawable.tutorialeight};
        this.id = id;
        counter = 0;
    }

    private void move(int direction) {
        counter += direction;
        if (counter == id.length)
            getActivity().onBackPressed();
        if (direction == -1 && counter == id.length - 2) {
            counter = 0;
            nextButton.setText("Next");
            previousButton.setText("Previous");
        }

        if (counter < 0)
            counter = 0;
        if (counter >= 0 && counter < id.length) {
            tutorialScreen.setImageResource(id[counter]);
        }
        if (counter == id.length - 1) {
            nextButton.setText("Main Menu");
            previousButton.setText("Restart");
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        tutorialScreen = (ImageView) view.findViewById(R.id.imageView);
        tutorialScreen.setImageResource(id[counter]);

        previousButton = (Button) view.findViewById(R.id.previousButton);
        previousButton.setTypeface(MainActivity.typefaceHeader);
        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                move(-1);
            }
        });
        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setTypeface(MainActivity.typefaceHeader);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                move(1);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTutorialInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTutorialInteractionListener) activity;
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
    public interface OnTutorialInteractionListener {
        // TODO: Update argument type and name
        public void onTutorialInteraction(Uri uri);
    }


}
