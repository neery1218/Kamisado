package com.radianceTOPS.constrain;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Michael on 8/4/2015.
 */
public class InteractiveTutorial extends Fragment {

    private TutorialView tutorialView ;

    public static TutorialFragment newInstance(String param1, String param2) {
        TutorialFragment fragment = new TutorialFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tutorial_interactive, container, false);

        tutorialView = (TutorialView)view.findViewById(R.id.tutorialView);
        return view;
    }

}
