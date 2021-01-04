package com.nurbiike.tk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.nurbiike.tk.R;

import static com.nurbiike.tk.ui.UtilMethods.setWindowStatus;
import static com.nurbiike.tk.ui.UtilMethods.setWindowStatusHidden;

public class SettingsActivity extends AppCompatActivity {
    private RadioGroup levelRG, startsRG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the status bar, set status bar color to black opaque
        setWindowStatus(getWindow());
        setWindowStatusHidden(getWindow());

        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Resources res = getResources();

        levelRG = findViewById(R.id.levelRBGroup);

        int searchdepth = sharedPref.getInt(getString(R.string.single_searchdepth), res.getInteger(R.integer.single_searchdepth_easy_level));

        if (searchdepth == res.getInteger(R.integer.single_searchdepth_easy_level)) {
            levelRG.check(R.id.levelEasyRb);
        } else if (searchdepth == res.getInteger(R.integer.single_searchdepth_medium_level)) {
            levelRG.check(R.id.levelMediumRb);
        } else if (searchdepth == res.getInteger(R.integer.single_searchdepth_hard_level)) {
            levelRG.check(R.id.levelHardRb);
        }

        //each time settings updated write it to shared preferences,
        //which can be read in gameplay
        levelRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if (radioButton.isChecked()) {

                    int level;
                    Resources res = getResources();
                    if (radioButton.getText().toString().equals(getString(R.string.level_easy_rb))) {
                        level = res.getInteger(R.integer.single_searchdepth_easy_level);
                    } else if (radioButton.getText().toString().equals(getString(R.string.level_medium_rb))) {
                        level = res.getInteger(R.integer.single_searchdepth_medium_level);
                    } else {
                        level = res.getInteger(R.integer.single_searchdepth_hard_level);
                    }

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt(getString(R.string.single_searchdepth), level);

                    editor.commit();
                }
            }
        });


        startsRG = findViewById(R.id.startGameRBGroup);

        //update ui according to saved settings
        String yesOrNoS = sharedPref.getString(getString(R.string.single_userstarts), res.getString(R.string.single_userstarts_yes));

        if (yesOrNoS.equals(res.getString(R.string.single_userstarts_yes))) {
            startsRG.check(R.id.yesRadioBtn);
        } else if (yesOrNoS.equals(res.getString(R.string.single_userstarts_no))) {
            startsRG.check(R.id.noRadioBtn);
        }

        //each time settings updated write it to shared preferences,
        //which can be read in gameplay
        startsRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if (radioButton.isChecked()) {

                    String yesOrNo;
                    Resources res = getResources();
                    if (radioButton.getText().toString().equals(getString(R.string.start_yes_rb))) {
                        yesOrNo = getString(R.string.single_userstarts_yes);
                    } else {
                        yesOrNo = getString(R.string.single_userstarts_no);
                    }

                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.single_userstarts), yesOrNo);

                    editor.commit();
                }
            }
        });

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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
    }
}