package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.SingleOptionsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SingleOptionsActivityTest {

    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<SingleOptionsActivity> mActivityTestRule = new ActivityTestRule<SingleOptionsActivity>(SingleOptionsActivity.class);

    private SingleOptionsActivity mSingleOptionsActivity = null;

    @Before
    public void setUp() throws Exception {
        mSingleOptionsActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vNineTextView = mSingleOptionsActivity.findViewById(R.id.nineTextView);
        View vToguzTextView = mSingleOptionsActivity.findViewById(R.id.toguzTextView);
        View vKorgoolTextView = mSingleOptionsActivity.findViewById(R.id.korgoolTextView);
        View vNewGameBtn = mSingleOptionsActivity.findViewById(R.id.newGameBtn);
        View vContinueBtn = mSingleOptionsActivity.findViewById(R.id.continueBtn);
        assertNotNull(vNineTextView);
        assertNotNull(vToguzTextView);
        assertNotNull(vKorgoolTextView);
        assertNotNull(vNewGameBtn);
        assertNotNull(vContinueBtn);
    }

    @Test
    public void verifyNewGameBtn() {
        onView(withId(R.id.newGameBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.GamePlayActivity")),
                toPackage(PACKAGE_NAME),
                hasExtra(mSingleOptionsActivity.getString(R.string.intent_key_mode),
                        mSingleOptionsActivity.getString(R.string.intent_val_mode_single)),
                hasExtra(mSingleOptionsActivity.getString(R.string.intent_key_option),
                        mSingleOptionsActivity.getString(R.string.intent_val_option_newgame))));
    }

    @Test
    public void verifyContinueBtn() {
        onView(withId(R.id.continueBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.GamePlayActivity")),
                toPackage(PACKAGE_NAME),
                hasExtra(mSingleOptionsActivity.getString(R.string.intent_key_mode),
                        mSingleOptionsActivity.getString(R.string.intent_val_mode_single)),
                hasExtra(mSingleOptionsActivity.getString(R.string.intent_key_option),
                        mSingleOptionsActivity.getString(R.string.intent_val_option_continue))));
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
