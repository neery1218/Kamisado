package com.radianceTOPS.constain;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.Toast;

public class GamePlayFragment extends Fragment implements GameControl.GameStateListener, GameBoardView.OnUndoToastCreate {

    private static int VERSUS_TYPE;
    private static int MATCH_TYPE;
    private static int AI_DIFFICULTY;
    private GameBoardView gameBoardView;
    private int layoutHeight;
    private RelativeLayout relativeLayout;

    private TextView scoreTextView1;
    private TextView seperatorTextView;
    private TextView scoreTextView2;
    private OnGamePlayInteractionListener mListener;

    private TextView screenTextView;
    private TextView subtitleTextView;

    private Typeface typeface;

    private LinearLayout holderLayout;

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

    private void setupUserBar(int player, View view) {

        //scoreTextView1.setLayoutParams(params);

        undoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                gameBoardView.undo();
            }
        });

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(layoutHeight, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams scoreViewParam = new LinearLayout.LayoutParams(width - layoutHeight, LinearLayout.LayoutParams.MATCH_PARENT);
        undoButton.setLayoutParams(buttonParams);
        LinearLayout scoreLayout = (LinearLayout)view.findViewById(R.id.scoreLayout);
        scoreLayout.setLayoutParams(scoreViewParam);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_play, container, false);
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/trajan.ttf");

        View content = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);//finds alloted screen size. this will save a lot of time.

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
                //screenLayout.removeAllViews();
                gameBoardView.removeScoreText();
            }
        });

        winId = 123;
        holderLayout = new LinearLayout(getActivity());
        holderLayout.setOrientation(LinearLayout.VERTICAL);

        screenTextView = new TextView(getActivity());
        screenTextView.setBackgroundColor(getResources().getColor(R.color.white));
        screenTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
        screenTextView.setTypeface(MainActivity.typefaceHeader);
        screenTextView.setTextColor(getResources().getColor(R.color.text));
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;

        subtitleTextView = new TextView(getActivity());
        subtitleTextView.setBackgroundColor(getResources().getColor(R.color.white));
        subtitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        subtitleTextView.setTypeface(MainActivity.typefaceHeader);
        subtitleTextView.setTextColor(getResources().getColor(R.color.text));

        screenTextView.setGravity(Gravity.CENTER);
        subtitleTextView.setGravity(Gravity.CENTER);

        holderLayout.setLayoutParams(param);
        holderLayout.addView(screenTextView);
        holderLayout.addView(subtitleTextView);

        screenLayout.addView(holderLayout);



        undoButton = (Button) view.findViewById(R.id.undoButton);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        scoreTextView1 = (TextView) view.findViewById(R.id.scoreTextView1);
        scoreTextView2 = (TextView) view.findViewById(R.id.scoreTextView2);
        seperatorTextView = (TextView) view.findViewById(R.id.seperatorTextView);
        scoreTextView1.setTypeface(typeface);
        scoreTextView2.setTypeface(typeface);
        seperatorTextView.setTypeface(typeface);
        scoreTextView1.setText("-");
        scoreTextView2.setText("-");
        seperatorTextView.setText(" / ");

        //compute layout sizes
        height = content.getHeight();
        width = content.getWidth();

        layoutHeight = (height - width) / 2;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutHeight);
        LinearLayout.LayoutParams gameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width);

        //set top linearlayout to contain textview
        titleTextView.setTypeface(MainActivity.typefaceHeader);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
        titleTextView.setTextColor(getResources().getColor(R.color.text));
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        titleTextView.setBackgroundColor(getResources().getColor(R.color.white));

        setupUserBar(GameControl.PLAYER_ONE, view);

        gameBoardView = (GameBoardView) view.findViewById(R.id.gameBoard);
        //gameBoardView needs to accept two views
        gameBoardView.setScoreTextView(scoreTextView1, scoreTextView2);
        gameBoardView.setLayoutParams(gameParams);
        gameBoardView.attachGameStateListener(this);
        gameBoardView.attachUndoToastCreate(this);


        // undoButton.setLayoutParams(params);
        //scoreTextView1.setRotation(180f); so other players can view
        //bottomUserLayout.addView(undoButton);
        // topUserLayout.addView(scoreTextView1);
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

    @Override
    public void p1Win(Point winPoint) {
        // while(gameBoardView.animationRunning){}
        // LinearLayout layout = new LinearLayout(getActivity());
        // screenLayout.addView(holderLayout);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("P1 wins round!");
        subtitleTextView.setText("Tap to Continue");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(300);


        screenTextView.setAnimation(in);
        subtitleTextView.setAnimation(in);


        // layoutParams.
        // layout.setLayoutParams();
    }

    @Override
    public void p2Win(Point winPoint) {
        // LinearLayout layout = new LinearLayout(getActivity());
        // screenLayout.addView(holderLayout);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("P2 wins round!");
        subtitleTextView.setText("Tap to Continue");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(300);


        screenTextView.setAnimation(in);
        subtitleTextView.setAnimation(in);
    }

    @Override
    public void deadlock(Point winPoint) {
        // LinearLayout layout = new LinearLayout(getActivity());
        //screenLayout.addView(holderLayout);
        relativeLayout.addView(screenLayout);

        screenTextView.setText("DEADLOCK");
        subtitleTextView.setText("Tap to Continue");
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(300);

        screenTextView.setAnimation(in);
        subtitleTextView.setAnimation(in);

    }

    @Override
    public void gameLimitReached(int player) {
        // LinearLayout layout = new LinearLayout(getActivity());

        if (player == GameControl.PLAYER_TWO)
            screenTextView.setText("P1 wins game!");
        else
            screenTextView.setText("P2 wins game!");

        subtitleTextView.setText("Tap to return to Main Menu");
        //screenLayout.addView(holderLayout);
        screenLayout.setOnClickListener(null);

        screenLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.done();
            }
        });
        relativeLayout.addView(screenLayout);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(100);


        screenTextView.setAnimation(in);

    }

    @Override
    public void onUndoToastCreate() {
        Toast undoToast = Toast.makeText(getActivity().getApplicationContext(), "Undo limit reached", Toast.LENGTH_SHORT);
        undoToast.show();
    }

    public interface OnGamePlayInteractionListener {
        // TODO: Update argument type and name
        public void done();
    }

}
