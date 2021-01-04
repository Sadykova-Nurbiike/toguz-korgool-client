package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.SettingsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SettingsActivityTest {
    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityTestRule = new ActivityTestRule<SettingsActivity>(SettingsActivity.class);

    private SettingsActivity mSettingsActivity = null;

    @Before
    public void setUp() throws Exception {
        mSettingsActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vDifficultyTextView = mSettingsActivity.findViewById(R.id.difficultyTextView);
        View vStartGameTextView = mSettingsActivity.findViewById(R.id.startGameTextView);
        View vDifficultyRBGroup = mSettingsActivity.findViewById(R.id.levelRBGroup);
        View vStartGameRBGroup = mSettingsActivity.findViewById(R.id.startGameRBGroup);
        assertNotNull(vDifficultyTextView);
        assertNotNull(vStartGameTextView);
        assertNotNull(vDifficultyRBGroup);
        assertNotNull(vStartGameRBGroup);
    }

    @Test
    public void verifyCheckingLevel() {
        onView(withId(R.id.levelMediumRb)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.levelEasyRb)).check(matches(not(isChecked())));
        onView(withId(R.id.levelHardRb)).check(matches(not(isChecked())));
    }

    @Test
    public void verifyCheckingStartGame() {
        onView(withId(R.id.noRadioBtn)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.yesRadioBtn)).check(matches(not(isChecked())));
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.nurbiike.tk", appContext.getPackageName());
    }

    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

}
