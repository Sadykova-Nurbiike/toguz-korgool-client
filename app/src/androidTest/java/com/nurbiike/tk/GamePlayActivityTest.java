package com.nurbiike.tk;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.GamePlayActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public class GamePlayActivityTest {
    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<GamePlayActivity> mActivityTestRule = new ActivityTestRule<GamePlayActivity>(GamePlayActivity.class);

    private GamePlayActivity mGamePlayActivity = null;

    @Before
    public void setUp() throws Exception {
        mGamePlayActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

}
