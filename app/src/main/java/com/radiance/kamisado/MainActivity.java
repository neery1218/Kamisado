package com.radiance.kamisado;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements IntroFragment.OnIntroInteractionListener, GamePlayFragment.OnGamePlayInteractionListener, TutorialFragment.OnTutorialInteractionListener, MatchLengthFragment.OnMatchLengthInteraction, AIDifficultyFragment.OnDifficultyInteraction {

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
    public static final String ARG_AI_DIFFICULTY = "ARG_AI_DIFFICULTY";
    public static Typeface typeFace;
    private int MATCH_TYPE = 0;
    private int AI_DIFFICULTY = 0;
    private int VERSUS_TYPE = 0;
    private IntroFragment introFragment;
    private GamePlayFragment gamePlayFragment;
    private TutorialFragment tutorialFragment;
    private MatchLengthFragment matchLengthFragment;
    private AIDifficultyFragment aiDifficultyFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        introFragment = new IntroFragment();//initializing first fragment being used
        typeFace = Typeface.createFromAsset(getAssets(), "NanumGothic-Regular.ttf");
        //initialize transaction and add to viewgroup
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, introFragment)
                .addToBackStack(null)
                .commit();

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
    public void onBackPressed() {
        fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fragment_container);
        if (f instanceof GamePlayFragment) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, introFragment)

                    .commit();
        } else {

            if (fragmentManager.getBackStackEntryCount() > 0)
                fragmentManager.popBackStack();
        }
        Log.v("Back", "number:" + fragmentManager.getBackStackEntryCount());


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
                fragmentTransaction.addToBackStack(null);
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
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.fragment_container, matchLengthFragment)
                    .addToBackStack(null)
                    .commit();
            // fragmentTransaction.replace(R.id.fragment_container, matchLengthFragment);
            // fragmentTransaction.commit();
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


        MATCH_TYPE = button;
        if (VERSUS_TYPE == PLAY_PRESSED) {
            aiDifficultyFragment = new AIDifficultyFragment();
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, aiDifficultyFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {//calls the listener that sets up gamePlayFragment
            onDifficultyInteraction(-1);
        }


    }

    @Override
    public void onDifficultyInteraction(int level) {
        AI_DIFFICULTY = level;
        gamePlayFragment = new GamePlayFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(ARG_MATCH_TYPE, MATCH_TYPE);
        bundle.putInt(ARG_VERSUS_TYPE, VERSUS_TYPE);

        if (AI_DIFFICULTY != -1)
            bundle.putInt(ARG_AI_DIFFICULTY, AI_DIFFICULTY);

        gamePlayFragment.setArguments(bundle);

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, gamePlayFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
