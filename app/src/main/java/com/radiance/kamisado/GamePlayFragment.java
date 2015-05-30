package com.radiance.kamisado;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GamePlayFragment extends Fragment implements GameControl.GameStateListener {

    private static int VERSUS_TYPE;
    private static int MATCH_TYPE;
    private static int AI_DIFFICULTY;
    private GameBoardView gameBoardView;
    private int layoutHeight;
    private RelativeLayout relativeLayout;

    private TextView scoreTextView;
    private OnGamePlayInteractionListener mListener;
    private TextView screenTextView;
    private int winId;

    private Button undoButton;
    private LinearLayout screenLayout;


    private TextView titleTextView;


    private LinearLayout[] userLayouts;

    private int height;
    private int width;



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

    public static int getAiDifficulty() {
        return AI_DIFFICULTY;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userLayouts = new LinearLayout[2];

        if (getArguments() != null) {
            VERSUS_TYPE = getArguments().getInt(MainActivity.ARG_VERSUS_TYPE);
            MATCH_TYPE = getArguments().getInt(MainActivity.ARG_MATCH_TYPE);
            if (VERSUS_TYPE == MainActivity.PLAY_PRESSED)
                AI_DIFFICULTY = getArguments().getInt(MainActivity.ARG_AI_DIFFICULTY);


        }
    }

    private void setupUserBar(int player) {


        //scoreTextView.setLayoutParams(params);

        undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gameBoardView.undo();
            }
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(layoutHeight, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams scoreViewParam = new LinearLayout.LayoutParams(width - layoutHeight, LinearLayout.LayoutParams.MATCH_PARENT);
        undoButton.setLayoutParams(buttonParams);
        scoreTextView.setLayoutParams(scoreViewParam);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);

        View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);//finds alloted screen size. this will save a lot of time.
        Log.v("UI", "Content: " + content.getWidth() + " " + content.getHeight());


        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        //player two has top layout, player one has bottom layout


        userLayouts[GameControl.PLAYER_ONE] = (LinearLayout) view.findViewById(R.id.bottomUserLayout);
        userLayouts[GameControl.PLAYER_TWO] = (LinearLayout) view.findViewById(R.id.topUserLayout);


        screenLayout = new LinearLayout(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        screenLayout.setLayoutParams(params);


        screenLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                relativeLayout.removeView(screenLayout);
                screenLayout.removeAllViews();

            }
        });

        winId = 123;
        screenTextView = new TextView(getActivity());
        screenTextView.setId(winId);
        screenTextView.setBackgroundColor(getResources().getColor(R.color.white));
        screenTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        screenTextView.setTypeface(MainActivity.typefaceHeader);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;

        screenTextView.setLayoutParams(param);
        screenTextView.setGravity(Gravity.CENTER);
        // screenTextView.setOnClickListener(this);



        undoButton = (Button) view.findViewById(R.id.undoButton);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        scoreTextView = (TextView) view.findViewById(R.id.scoreTextView);

        //compute layout sizes
        height = content.getHeight();
        width = content.getWidth();

        layoutHeight = (height - width) / 2;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutHeight);
        LinearLayout.LayoutParams gameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);

        //set top linearlayout to contain textview
        titleTextView.setText("Kamisado");
        titleTextView.setTypeface(MainActivity.typefaceHeader);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
        titleTextView.setTextColor(getResources().getColor(R.color.text));
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        titleTextView.setBackgroundColor(getResources().getColor(R.color.white));

        setupUserBar(GameControl.PLAYER_ONE);


        gameBoardView = (GameBoardView) view.findViewById(R.id.gameBoard);
        //gameBoardView needs to accept two views
        gameBoardView.setScoreView(scoreTextView);
        gameBoardView.setLayoutParams(gameParams);
        gameBoardView.attachGameStateListener(this);


        // undoButton.setLayoutParams(params);
        //scoreTextView.setRotation(180f); so other players can view
        //bottomUserLayout.addView(undoButton);
        // topUserLayout.addView(scoreTextView);
        userLayouts[GameControl.PLAYER_ONE].setLayoutParams(layoutParams);
        userLayouts[GameControl.PLAYER_TWO].setLayoutParams(layoutParams);
        // userLayout

        //topUserLayout.setLayoutParams(layoutParams);
        //bottomUserLayout.setLayoutParams(layoutParams);
        //topUserLayout.addView(undoButton);

        return view;
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

   /* @Override
    public void onClick(View v) {
        if (v.getId() == R.id.undoButton) {
            gameBoardView.undo();
            Log.v("INTERFACE", "undo called");
        }

        if (v.getId() == winId) {
            Log.v("INTERFACE", "remove View called");

            relativeLayout.removeView(screenLayout);
            screenLayout.removeAllViews();
        }
        //screenTextView.setVisibility(View.GONE);


    }*/

    @Override
    public void p1Win(Point winPoint) {
        // while(gameBoardView.animationRunning){}
        Log.d("INTERFACE", "p1win called");
        // LinearLayout layout = new LinearLayout(getActivity());
        screenLayout.addView(screenTextView);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("Player one wins!");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(100);


        screenTextView.setAnimation(in);


        // layoutParams.
        // layout.setLayoutParams();
    }

    @Override
    public void p2Win(Point winPoint) {
        Log.d("INTERFACE", "p2win called");
        // LinearLayout layout = new LinearLayout(getActivity());
        screenLayout.addView(screenTextView);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("Player two wins!");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(100);


        screenTextView.setAnimation(in);
    }

    @Override
    public void deadlock(Point winPoint) {
        Log.d("INTERFACE", "deadlock called");
        // LinearLayout layout = new LinearLayout(getActivity());
        screenLayout.addView(screenTextView);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("DEADLOCK");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(100);


        screenTextView.setAnimation(in);

    }

    @Override
    public void gameLimitReached(int player) {
        Log.d("INTERFACE", "gameLimitReached called");
        // LinearLayout layout = new LinearLayout(getActivity());

        if (player == GameControl.PLAYER_ONE)
            screenTextView.setText("P1 wins game");
        else
            screenTextView.setText("P2 wins game");

        screenLayout.addView(screenTextView);
        screenLayout.setOnClickListener(null);

        screenLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.done();
            }
        });
        relativeLayout.addView(screenLayout);

        screenTextView.setText("Player one wins!");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(100);


        screenTextView.setAnimation(in);

    }

    public interface OnGamePlayInteractionListener {
        // TODO: Update argument type and name
        public void done();
    }

}
