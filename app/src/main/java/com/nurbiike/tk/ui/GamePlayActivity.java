package com.nurbiike.tk.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nurbiike.tk.R;
import com.nurbiike.tk.logic.GameStateSingle;
import com.nurbiike.tk.logic.Move;
import com.nurbiike.tk.logic.GameStateMultiPlayer;
import com.nurbiike.tk.logic.MoveSound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.nurbiike.tk.ui.UtilMethods.setWindowStatus;
import static com.nurbiike.tk.ui.UtilMethods.setWindowStatusHidden;

public class GamePlayActivity extends AppCompatActivity {

    //GUI
    private ImageView[] holesImages;
    private TextView[] holesTexts, holesLabels;
    private ImageView kazanImageView1, kazanImageView2;
    private TextView scoreTextView;
    private ImageView turnStickImage1, turnStickImage2;

    private ImageView compIsThinkingImg;
    private TextView infoMsgTextView;
    private EditText gameIDEditText;
    private Button gameIDOkBtn;
    private LinearLayout gameIDlayout;

    private int lastMovedHole = -1;

    private MoveSound lastMoveSound;
    private MediaPlayer mpMove, mpGain, mpPlus15Gain, mpTuz;
    private boolean soundOn;

    public Intent intent;

    //GamePlay Thread
    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(false);

    //COMMON
    public String gameMode, gameType, gameOption;
    public String input;

    //MULTI
    public String serverIp;
    public GameStateMultiPlayer multiState;
    BufferedReader socketIn;
    PrintWriter socketOut;
    Socket s;
    String gameID, gameIDEditTextVal;

    //SINGLE
    private String userStarts;
    private static int SEARCH_DEPTH;
    public GameStateSingle singleState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar, set status bar color to black opaque
        setWindowStatus(getWindow());
        setWindowStatusHidden(getWindow());

        setContentView(R.layout.activity_gameplay);

        //linking to Views
        holesImages = new ImageView[18];
        holesTexts = new TextView[18];
        holesLabels = new TextView[18];
        for (int i = 0; i < 18; i++) {
            int imageId = getResources().getIdentifier("imageView" + i, "id", getPackageName());
            int textId = getResources().getIdentifier("cntTextView" + i, "id", getPackageName());
            int labelId = getResources().getIdentifier("numTextView" + i, "id", getPackageName());
            holesImages[i] = findViewById(imageId);
            holesTexts[i] = findViewById(textId);
            holesLabels[i] = findViewById(labelId);
        }

        kazanImageView1 = findViewById(R.id.imageViewS1);
        kazanImageView2 = findViewById(R.id.imageViewS2);
        scoreTextView = findViewById(R.id.scoreLabel);
        turnStickImage1 = findViewById(R.id.turnStickImageD);
        turnStickImage2 = findViewById(R.id.turnStickImageU);
        infoMsgTextView = findViewById(R.id.infoMsgTextView);

        compIsThinkingImg = findViewById(R.id.compIsThinkingImg);
        Glide.with(this)
                .load(R.drawable.load_small)
                .into(compIsThinkingImg);

        Glide.with(this)
                .load(R.drawable.load_big)
                .into((ImageView) findViewById(R.id.loadingPanel));

        gameIDEditText = findViewById(R.id.gameIDEditText);
        gameIDEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = gameIDEditText.getText().toString().trim();

                gameIDOkBtn.setEnabled(!text.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        gameIDOkBtn = findViewById(R.id.gameIDOkBtn);
        gameIDOkBtn.setEnabled(false);
        gameIDlayout = findViewById(R.id.gameIdLayout);
        gameIDlayout.setVisibility(View.GONE);

        //Sounds
        soundOn = true;
        mpMove = MediaPlayer.create(this, R.raw.move);
        mpMove.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mpMove.seekTo(0);
            }
        });
        mpGain = MediaPlayer.create(this, R.raw.gain);
        mpGain.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mpGain.seekTo(0);
            }
        });
        mpPlus15Gain = MediaPlayer.create(this, R.raw.plus15gain);
        mpPlus15Gain.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mpPlus15Gain.seekTo(0);
            }
        });
        mpTuz = MediaPlayer.create(this, R.raw.tuz);
        mpTuz.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mpTuz.seekTo(0);
            }
        });


        //Reading info from intent
        intent = getIntent();
        gameMode = intent.getStringExtra(getString(R.string.intent_key_mode));

        if (gameMode.equals(getString(R.string.intent_val_mode_single))) {
            gameOption = intent.getStringExtra(getString(R.string.intent_key_option));
            changeViewsVisibility(true);
            showInfoMsg("", false);
        } else { //multi
            gameType = intent.getStringExtra(getString(R.string.intent_key_type));

            if (gameType.equals(getString(R.string.intent_val_type_multi_local))) {
                gameOption = intent.getStringExtra(getString(R.string.intent_key_option));
                changeViewsVisibility(true);
                showInfoMsg("", false);
            } else if (gameType.equals(getString(R.string.intent_val_type_multi_int_rand))) {
                changeViewsVisibility(false);
            } else if (gameType.equals(getString(R.string.intent_val_type_multi_int_spec))) {
                gameOption = intent.getStringExtra(getString(R.string.intent_key_option));
                if (gameOption.equals(getString(R.string.intent_val_option_joingame))) {
                    gameIDlayout.setVisibility(View.VISIBLE);
                    showInfoMsg("", false);
                    changeViewsVisibility(false);
                } else {
                    changeViewsVisibility(false);
                }
            }
        }
        //Reading info from intent


        startGamePlayThread();
    }


    /////-------------------------------Common------------------------------- /////
    //shows gameplay views and hides other views
    public void changeViewsVisibility(boolean showGamePlayViews) {
        for (int i = 0; i < 18; i++) {
            holesImages[i].setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
            holesTexts[i].setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
            holesLabels[i].setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        }
        kazanImageView1.setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        kazanImageView2.setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        scoreTextView.setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        turnStickImage1.setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        turnStickImage2.setVisibility(View.GONE);

        findViewById(R.id.scoreLabel).setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        findViewById(R.id.soundBtn).setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);
        compIsThinkingImg.setVisibility(showGamePlayViews ? View.VISIBLE : View.GONE);

        infoMsgTextView.setVisibility(!showGamePlayViews ? View.VISIBLE : View.GONE);
    }

    public void showInfoMsg(String infoMsg, boolean showLoadingImg) {
        infoMsgTextView.setText(infoMsg);
        findViewById(R.id.loadingPanel).setVisibility(showLoadingImg ? View.VISIBLE : View.GONE);
    }

    public void holeClicked(View view) {
        ImageView holeImage = (ImageView) view;
        String num = holeImage.getTag().toString();
        inputHoleChoice(num);
    }

    public void soundBtnHandler(View view) {
        ImageView soundImageView = (ImageView) view;
        if (soundOn) {
            soundImageView.setImageResource(getResources().getIdentifier(
                    "mute", "drawable", getPackageName()));
        } else {
            soundImageView.setImageResource(getResources().getIdentifier(
                    "music", "drawable", getPackageName()));
        }
        soundOn = !soundOn;
    }

    // used from the GUI to overwrite the values of input(chosen hole of the user)
    public void inputHoleChoice(String choice) {
        this.input = choice;
    }

    private void gameOverAlertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle("Game Over!");
        dialog.setPositiveButton("home",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        //Redirect to home activity
                        Intent myIntent = new Intent(GamePlayActivity.this, HomeActivity.class);
                        GamePlayActivity.this.startActivity(myIntent);
                    }
                });
        dialog.setNegativeButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Exit app
                GamePlayActivity.this.finish();
                System.exit(0);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    //it retrieves the balls from a hole on the board
    private int getBallsFromBoard(int hole_number) {
        return gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.getCnt(hole_number) : singleState.getCnt(hole_number);
    }

    //update images, labels with corresponding balls in the holes on the board
    public void updateBoardUI() {

        boolean turnStickBool = gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.turnStickUp : singleState.maxTurn;
        //tell user who's turn is it, by changing visibility of turnStick
        if (turnStickBool) {
            turnStickImage2.setVisibility(View.VISIBLE);
            turnStickImage1.setVisibility(View.INVISIBLE);
        } else {
            turnStickImage1.setVisibility(View.VISIBLE);
            turnStickImage2.setVisibility(View.INVISIBLE);
        }

        //show comp is thinking only when user is waiting for comp/opponent over the internet
        if (((singleState != null && singleState.maxTurn)
                || (multiState != null && !gameType.equals(getString(R.string.intent_val_type_multi_local)) && multiState.maxTurn))) {
            compIsThinkingImg.setVisibility(View.VISIBLE);
        } else {
            compIsThinkingImg.setVisibility(View.GONE);
        }

        //update image and text of board elems
        for (int i = 0; i < 18; i++) {
            int balls = getBallsFromBoard(i);
            holesTexts[i].setText(Integer.toString(balls));
            holesImages[i].setBackgroundColor(0);
            if (balls > 25) {
                balls = 25;
            }
            if (balls == 0 || balls == 10) {
                holesImages[i].setImageResource(getResources().getIdentifier(
                        "ud" + balls, "drawable", getPackageName()));
            } else if (i >= 0 && i <= 8) {
                holesImages[i].setImageResource(getResources().getIdentifier(
                        "u" + balls, "drawable", getPackageName()));
            } else if (i >= 9 && i <= 17) {
                holesImages[i].setImageResource(getResources().getIdentifier(
                        "d" + balls, "drawable", getPackageName()));
            }
        }

        //make valid moved hole to flash
        if (lastMovedHole != -1) {
            holesImages[lastMovedHole].setBackgroundColor(Color.parseColor("#FFFFCC"));
        }

        //make appropriate sound
        if (soundOn) {
            if (lastMoveSound == MoveSound.MOVE) {
                mpMove.start();
            } else if (lastMoveSound == MoveSound.GAIN) {
                mpGain.start();
            }
            if (lastMoveSound == MoveSound.PLUS15GAIN) {
                mpPlus15Gain.start();
            }
            if (lastMoveSound == MoveSound.TUZ) {
                mpTuz.start();
            }
        }

        //show the score, change kazan imgs
        int s1 = gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.kazan[0] : singleState.kazan[0];
        int s2 = gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.kazan[1] : singleState.kazan[1];
        scoreTextView.setText(s2 + " : " + s1);
        s1 = s1 > 89 ? 89 : s1;
        s2 = s2 > 89 ? 89 : s2;
        kazanImageView1.setImageResource(getResources().getIdentifier(
                "s" + s1, "drawable", getPackageName()));
        kazanImageView2.setImageResource(getResources().getIdentifier(
                "s" + s2, "drawable", getPackageName()));

        for (int i = 0; i < 2; i++) {
            if ((gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.tuz[i] : singleState.tuz[i]) != -1) {
                holesImages[(gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.tuz[i] : singleState.tuz[i])].setImageResource(getResources().getIdentifier(
                        "tuz", "drawable", getPackageName()));
                holesTexts[(gameMode.equals(getString(R.string.intent_val_mode_multi)) ? multiState.tuz[i] : singleState.tuz[i])].setText("0");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveToMainActivity();
    }

    private void moveToMainActivity() {
        if (gameMode.equals(getString(R.string.intent_val_mode_multi)) && gameType.equals(getString(R.string.intent_val_type_multi_local))) {
            saveMultiState();
        } else if (gameMode.equals(getString(R.string.intent_val_mode_single))) { //TODO check if single mode
            saveSingleState();
        }

        startActivity(new Intent(GamePlayActivity.this, HomeActivity.class));
        //finish();
        stopGamePlayThread();

        //Release MediasPlayers to free up space
        mpTuz.release();
        mpPlus15Gain.release();
        mpGain.release();
        mpMove.release();

        System.exit(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();

        if (y < 20 && event.getAction() == MotionEvent.ACTION_DOWN) {
            setWindowStatus(getWindow());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            setWindowStatusHidden(getWindow());
        }

        return false;
    }


    /////-------------------------------Gameplay-Thread------------------------------- /////
    public void startGamePlayThread() {
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                running.set(true);

                try {
                    if (gameMode.equals(getString(R.string.intent_val_mode_single))) {
                        gamePlaySingle();
                    } else if (gameMode.equals(getString(R.string.intent_val_mode_multi))) {
                        if (gameType.equals(getString(R.string.intent_val_type_multi_local))) {
                            gamePlayMultiLocal();
                        } else if (gameType.equals(getString(R.string.intent_val_type_multi_int_rand))) {
                            gamePlayMultiInt();
                        } else if (gameType.equals(getString(R.string.intent_val_type_multi_int_spec))) {
                            gamePlayMultiInt();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        worker.start();
    }

    public void stopGamePlayThread() {
        running.set(false);
    }


    /////-------------------------------Multiplayer-Internet------------------------------- /////
    public void gamePlayMultiInt() throws InterruptedException, IOException {

        //IP of the server, NOTE that it is hard code server ip, it should be overwritten by actual server ip
        serverIp = "192.168.0.241"; 

        /*try to connect to the server. 
        if not, it shows an error message and the program quits*/
        try {
            s = new Socket(serverIp, 9000);
            socketIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            socketOut = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException e1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showInfoMsg("Unable to connect to the server. Please try again.", false);
                }
            });
            Thread.sleep(3000);
            moveToMainActivity();
            socketOut.flush();
        }

        int startHole = 0;
        int endHole = 0;
        String message;


        //IF Random
        if (gameType.equals(getString(R.string.intent_val_type_multi_int_rand))) {
            socketOut.println(getString(R.string.intent_val_type_multi_int_rand));
            socketOut.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showInfoMsg("Waiting for the opponent to join.", true);
                }
            });

        } else {
            socketOut.println(getString(R.string.intent_val_type_multi_int_spec));
            socketOut.flush();

            if (gameOption.equals(getString(R.string.intent_val_option_creategame))) {
                socketOut.println(getString(R.string.intent_val_option_creategame));
                socketOut.flush();

                gameID = socketIn.readLine();

                if (gameID != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfoMsg("Waiting for the opponent to join.\n Game ID: " + gameID.trim(), true);
                        }
                    });
                }
            } else {
                socketOut.println(getString(R.string.intent_val_option_joingame));
                socketOut.flush();

                gameIDEditTextVal = null;
                while (gameIDEditTextVal == null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gameIDlayout.setVisibility(View.GONE);
                    }
                });

                socketOut.println(gameIDEditTextVal);
                socketOut.flush();
            }
        }

        String startGame = socketIn.readLine();

        Log.i("info", startGame);
        while (true) {

            if (startGame.trim().equals("NOTFOUND")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfoMsg("There is no game with ID " + gameIDEditTextVal + ".\nPlease, try again", false);
                    }
                });
                Thread.sleep(3000);
                moveToMainActivity();
            }

            /*following two if statements check the starting position of the player according
            to the type of START message that the server
            will send to the client.*/
            if (startGame.trim().equals("START1")) {
                startHole = 9;
                endHole = 17;
                multiState = new GameStateMultiPlayer(1);
                multiState.maxTurn = false;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfoMsg("You are player 1. LOWER row is yours", false);
                    }
                });

                break;
            } else if (startGame.trim().equals("START2")) {
                startHole = 0;
                endHole = 8;
                multiState = new GameStateMultiPlayer(2);
                multiState.maxTurn = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfoMsg("You are player 2. UPPER row is yours", false);
                    }
                });

                break;
            }
        }
        Thread.sleep(3000);

        multiState.turnStickUp = false;

        //refresh the GUI
        if (running.get()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("Error",
                        "Thread was interrupted, Failed to complete operation");
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateBoardUI();
                    changeViewsVisibility(true);
                }
            });
        }

        while (!multiState.gameOver() && running.get()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("Error",
                        "Thread was interrupted, Failed to complete operation");
            }


            /*This players Turn. We wait until input's value is not null anymore and then
            checks if input is a valid move.
            if yes, then move is done
            if no, the user is asked to try again*/
            if (!multiState.maxTurn) {
                int pool = 0;
                do {
                    try {
                        input = null;
                        while (input == null) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                            }
                        }
                        pool = Integer.parseInt(input);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                } while ((pool < startHole || pool > endHole || !multiState.isLegalMove(pool)) && running.get());

                /*if the move is accepted, it makes it on this machine then it makes a message
                that is send to the server and from there
                the server sends it further.
                According to the protocol the message should be a integer which is the
                chosen move.*/
                lastMovedHole = pool;
                lastMoveSound = multiState.makeMove(new Move(pool));

                message = String.valueOf(pool);

                socketOut.println(message);
                socketOut.flush();
            } // Opponent's Turn
            else {
                /*Waits to receive a message from opponent. If it is EXIT it means the opponent
                has left and the program is terminated.
                Otherwise comes an integer with the opponents move*/
                try {
                    String opponentsMove = socketIn.readLine().trim();
                    Log.i("info", opponentsMove);
                    if (opponentsMove.trim().equals("EXIT")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showInfoMsg("Your opponent has left the game :(.", false);
                                changeViewsVisibility(false);
                            }
                        });
                        Thread.sleep(3000);
                        s.close();
                        moveToMainActivity();
                        stopGamePlayThread();
                    } else {
                        int pool2 = Integer.parseInt(opponentsMove);
                        lastMovedHole = pool2;
                        lastMoveSound = multiState.makeMove(new Move(pool2));
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showInfoMsg("Your opponent has left the game :(.", false);
                            changeViewsVisibility(false);
                        }
                    });
                    Thread.sleep(3000);
                    s.close();
                    moveToMainActivity();
                }
            }

            //refresh the GUI
            multiState.turnStickUp = !multiState.turnStickUp;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateBoardUI();
                }
            });
        }

        //shows the result of the game to the user
        int winner = multiState.whoIsWinner();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoardUI();
            }
        });
        Thread.sleep(3000);
        final String gameOverMsg;
        if (winner == 1) {
            gameOverMsg = "You won!\nScore " + scoreTextView.getText() + ".";
        } else if (winner == -1) {
            gameOverMsg = "Opponent has won. \nScore " + scoreTextView.getText() + ".";
        } else {
            gameOverMsg = "DRAW!";
        }
        runOnUiThread(new Runnable() {
            public void run() {
                gameOverAlertDialog(gameOverMsg);
            }
        });
        Thread.sleep(3000);
    }

    public void handleGameIDOKBtn(View view) {
        gameIDEditTextVal = gameIDEditText.getText().toString().trim();
    }

    public void handleGameIDCancelBtn(View view) {
        moveToMainActivity();
    }


    /////-------------------------------Multiplayer-Local------------------------------- /////
    public void gamePlayMultiLocal() throws InterruptedException, IOException {

        //INITIALIZE NEW GAME STATE
        multiState = new GameStateMultiPlayer();
        multiState.turnStickUp = false;
        multiState.maxTurn = false;

        //INITIALIZE SAVED GAME STATE
        if (gameOption.equals(getString(R.string.intent_val_option_continue))) {
            readAssignSavedMultiState();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoardUI();
            }
        });

        Log.i("info", "\n THE GAME IS STARTING... \n");

        while (!multiState.gameOver() && running.get()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("Error",
                        "Thread was interrupted, Failed to complete operation");
            }

            // Player1's Turn
            if (!multiState.maxTurn) {
                int hole = 0;
                Log.i("info", "Player1's TURN\n");
                do {
                    input = null;
                    while (input == null) { //waiting until the user press the button
                        try {
                            Thread.sleep(100);
                        } catch (final InterruptedException e) {
                        }
                    }

                    hole = Integer.parseInt(input);
                } while (!multiState.isLegalMove(hole) || hole < 9);

                lastMovedHole = hole;
                lastMoveSound = multiState.makeMove(new Move(hole));
            }
            // Player2's Turn
            else {
                int hole = 0;
                Log.i("info", "Player2's TURN\n");
                do {
                    input = null;
                    while (input == null) { //waiting until the user press the button.
                        try {
                            Thread.sleep(100);
                        } catch (final InterruptedException e) {
                        }
                    }

                    hole = Integer.parseInt(input);
                } while (!multiState.isLegalMove(hole) || hole > 8);

                lastMovedHole = hole;
                lastMoveSound = multiState.makeMove(new Move(hole));
            }

            multiState.turnStickUp = !multiState.turnStickUp;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateBoardUI();
                }
            });
        }

        //shows the result of the game to the user
        int winner = multiState.whoIsWinner();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoardUI();
            }
        });
        Thread.sleep(3000);
        final String gameOverMsg;
        if (winner == 1) {
            gameOverMsg = "Player 1 won!\nScore " + scoreTextView.getText() + ".";
        } else if (winner == -1) {
            gameOverMsg = "Player 2 won. \nScore " + scoreTextView.getText() + ".";
        } else {
            gameOverMsg = "DRAW!";
        }
        runOnUiThread(new Runnable() {
            public void run() {
                gameOverAlertDialog(gameOverMsg);
            }
        });
        Thread.sleep(3000);
    }

    //saves the multi local game state to SharedPreferences
    public void saveMultiState() {
        SharedPreferences sharedPref = GamePlayActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(getString(R.string.multi_maxturn), multiState.maxTurn);
        editor.putBoolean(getString(R.string.multi_turnstickup), multiState.turnStickUp);

        String boardS = "";
        for (int i = 0; i < multiState.board.length; i++) {
            boardS = boardS + multiState.board[i] + (i < 17 ? "," : "");
        }
        editor.putString(getString(R.string.multi_board), boardS);

        String kazanS = multiState.kazan[0] + "," + multiState.kazan[1];
        editor.putString(getString(R.string.multi_kazan), kazanS);

        String tuzS = multiState.tuz[0] + "," + multiState.tuz[1];
        editor.putString(getString(R.string.multi_tuz), tuzS);

        editor.commit();
    }

    //restores the multi local game state from SharedPreferences
    public void readAssignSavedMultiState() {
        SharedPreferences sharedPref = GamePlayActivity.this.getPreferences(Context.MODE_PRIVATE);

        Resources res = getResources();
        multiState.maxTurn = sharedPref.getBoolean(getString(R.string.multi_maxturn), res.getBoolean(R.bool.maxturn_def_val));
        multiState.turnStickUp = sharedPref.getBoolean(getString(R.string.multi_turnstickup), res.getBoolean(R.bool.multi_turnstickup_def_val));

        String boardS = sharedPref.getString(getString(R.string.multi_board), getString(R.string.board_def_val));
        String[] arrBoardS = boardS.split(",", 18);

        for (int i = 0; i < 18; i++) {
            System.out.println(i + " " + arrBoardS[i]);
            multiState.board[i] = Integer.parseInt(arrBoardS[i]);
        }

        String kazanS = sharedPref.getString(getString(R.string.multi_kazan), getString(R.string.kazan_def_val));
        String[] arrKazanS = kazanS.split(",");
        multiState.kazan[0] = Integer.parseInt(arrKazanS[0]);
        multiState.kazan[1] = Integer.parseInt(arrKazanS[1]);

        String tuzS = sharedPref.getString(getString(R.string.multi_tuz), getString(R.string.tuz_def_val));
        String[] arrTuzS = tuzS.split(",");
        multiState.tuz[0] = Integer.parseInt(arrTuzS[0]);
        multiState.tuz[1] = Integer.parseInt(arrTuzS[1]);

    }


    /////-------------------------------Single-Player------------------------------- /////
    public void gamePlaySingle() throws InterruptedException {

        //INITIALIZE NEW GAME STATE
        singleState = new GameStateSingle();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Resources res = getResources();
        SEARCH_DEPTH = sharedPref.getInt(getString(R.string.single_searchdepth), res.getInteger(R.integer.single_searchdepth_easy_level));
        userStarts = sharedPref.getString(getString(R.string.single_userstarts), getString(R.string.single_userstarts_yes));


        //INITIALIZE SAVED GAME STATE
        if (gameOption.equals(getString(R.string.intent_val_option_newgame))) {
            singleState.maxTurn = !"y".equalsIgnoreCase(userStarts);
        } else { //continue option
            readAssignSavedSingleState();
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoardUI();
            }
        });

        Log.i("info", "\n THE GAME IS STARTING \n");

        while (!singleState.gameOver() && running.get()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("Error",
                        "Thread was interrupted, Failed to complete operation");
            }

            // Human's Turn
            if (!singleState.maxTurn) {
                int hole = 0;
                Log.i("info", "YOUR TURN\n");
                do {
                    input = null;
                    while (input == null) { //waiting until the user press the button
                        try {
                            Thread.sleep(100);
                        } catch (final InterruptedException e) {
                        }
                    }

                    hole = Integer.parseInt(input);
                } while (!singleState.isLegalMove(hole) || hole < 9);

                lastMovedHole = hole;
                lastMoveSound = singleState.makeMove(new Move(hole));
            }
            // Computer's Turn
            else {

                Log.i("info", "Computer's Turn! Thinking...\n");
                final double time1 = System.currentTimeMillis();
                Thread.sleep(3000);
                Move bestMove = singleState.getBestMove(SEARCH_DEPTH);
                lastMovedHole = bestMove.getFromHole();
                lastMoveSound = singleState.makeMove(bestMove);
                Thread.sleep(3000);
                final double time2 = System.currentTimeMillis();
                Log.i("info", "SEARCHING TIME: " + (time2 - time1) / 1000 + " seconds");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateBoardUI();
                }
            });
        }

        //shows the result of the game to the user
        int winner = singleState.whoIsWinner();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoardUI();
            }
        });
        Thread.sleep(3000);
        final String gameOverMsg;
        if (winner == 1) {
            gameOverMsg = "You won!\nScore " + scoreTextView.getText() + ".";
        } else if (winner == -1) {
            gameOverMsg = "Computer has won. \nScore " + scoreTextView.getText() + ".";
        } else {
            gameOverMsg = "DRAW!";
        }
        runOnUiThread(new Runnable() {
            public void run() {
                gameOverAlertDialog(gameOverMsg);
            }
        });
        Thread.sleep(3000);

    }

    //saves the multi local game state to SharedPreferences
    public void saveSingleState() {
        SharedPreferences sharedPref = GamePlayActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(getString(R.string.single_maxturn), singleState.maxTurn);
        editor.putInt(getString(R.string.single_searchdepth), SEARCH_DEPTH);

        String boardS = "";
        for (int i = 0; i < singleState.board.length; i++) {
            boardS = boardS + singleState.board[i] + (i < 17 ? "," : "");
        }
        editor.putString(getString(R.string.single_board), boardS);

        String kazanS = singleState.kazan[0] + "," + singleState.kazan[1];
        editor.putString(getString(R.string.single_kazan), kazanS);

        String tuzS = singleState.tuz[0] + "," + singleState.tuz[1];
        editor.putString(getString(R.string.single_tuz), tuzS);

        editor.commit();
    }

    //restores the single game state from SharedPreferences
    public void readAssignSavedSingleState() {
        SharedPreferences sharedPref = GamePlayActivity.this.getPreferences(Context.MODE_PRIVATE);

        Resources res = getResources();
        singleState.maxTurn = sharedPref.getBoolean(getString(R.string.single_maxturn), res.getBoolean(R.bool.maxturn_def_val));

        String boardS = sharedPref.getString(getString(R.string.single_board), getString(R.string.board_def_val));
        String[] arrBoardS = boardS.split(",", 18);

        for (int i = 0; i < 18; i++) {
            System.out.println(i + " " + arrBoardS[i]);
            singleState.board[i] = Integer.parseInt(arrBoardS[i]);
        }

        String kazanS = sharedPref.getString(getString(R.string.single_kazan), getString(R.string.kazan_def_val));
        String[] arrKazanS = kazanS.split(",");
        singleState.kazan[0] = Integer.parseInt(arrKazanS[0]);
        singleState.kazan[1] = Integer.parseInt(arrKazanS[1]);

        String tuzS = sharedPref.getString(getString(R.string.single_tuz), getString(R.string.tuz_def_val));
        String[] arrTuzS = tuzS.split(",");
        singleState.tuz[0] = Integer.parseInt(arrTuzS[0]);
        singleState.tuz[1] = Integer.parseInt(arrTuzS[1]);

    }

}