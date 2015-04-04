package com.radiance.kamisado;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements IntroFragment.OnIntroInteractionListener, GamePlayFragment.OnGamePlayInteractionListener, TutorialFragment.OnTutorialInteractionListener, MatchLengthFragment.OnMatchLengthInteraction {

    public static final int PLAY_PRESSED = 0;
    public static final int TUTORIAL_PRESSED = 1;
    public static final int TWO_PLAY_PRESSED = 2;
    public static final int ONLINE_PLAY_PRESSED = 3;

    public static final int MATCH_SINGLE = 1;
    public static final int MATCH_STANDARD = 3;
    public static final int MATCH_EXTENDED = 7;
    public static final int MATCH_MARATHON = 15;

    public static final String ARG_MATCH_TYPE = "ARG_MATCH_TYPE";
    public static final String ARG_VERSUS_TYPE = "ARG_VERSUS_TYPE";

    private int VERSUS_TYPE = 0;

    private IntroFragment introFragment;
    private GamePlayFragment gamePlayFragment;
    private TutorialFragment tutorialFragment;
    private MatchLengthFragment matchLengthFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        introFragment = new IntroFragment();//initializing first fragment being used

        //initialize transaction and add to viewgroup
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, introFragment);
        fragmentTransaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIntroInteraction(int button) {

        Bundle bundle = new Bundle();
        boolean tutorialPressed = false;

        switch (button){
            case PLAY_PRESSED:
                VERSUS_TYPE = button;
                break;
            case TUTORIAL_PRESSED:
                tutorialFragment = new TutorialFragment();
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, tutorialFragment);
                fragmentTransaction.commit();
                tutorialPressed = true;
                break;
            case TWO_PLAY_PRESSED:
                VERSUS_TYPE = button;
                break;
            case ONLINE_PLAY_PRESSED:
                VERSUS_TYPE = button;
                break;
        }
        if (!tutorialPressed){
            matchLengthFragment = new MatchLengthFragment();
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, matchLengthFragment);
            fragmentTransaction.commit();
        }


    }

    @Override
    public void onGamePlayInteraction(Uri uri) {

    }

    @Override
    public void onTutorialInteraction(Uri uri) {

    }

    @Override
    public void onMatchLengthInteraction(int button) {

        gamePlayFragment = new GamePlayFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MATCH_TYPE,button);
        bundle.putInt(ARG_VERSUS_TYPE,VERSUS_TYPE);

        gamePlayFragment.setArguments(bundle);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, gamePlayFragment);
        fragmentTransaction.commit();
    }
}
