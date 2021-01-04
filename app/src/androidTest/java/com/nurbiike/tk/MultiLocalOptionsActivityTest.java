package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.MultiLocalOptionsActivity;

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

public class MultiLocalOptionsActivityTest {
    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<MultiLocalOptionsActivity> mActivityTestRule = new ActivityTestRule<MultiLocalOptionsActivity>(MultiLocalOptionsActivity.class);

    private MultiLocalOptionsActivity mMultiLocalOptionsActivity = null;

    @Before
    public void setUp() throws Exception {
        mMultiLocalOptionsActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vNineTextView = mMultiLocalOptionsActivity.findViewById(R.id.nineTextView);
        View vToguzTextView = mMultiLocalOptionsActivity.findViewById(R.id.toguzTextView);
        View vKorgoolTextView = mMultiLocalOptionsActivity.findViewById(R.id.korgoolTextView);
        View vNewGameBtn = mMultiLocalOptionsActivity.findViewById(R.id.multiOptionsNewGameBtn);
        View vContinueBtn = mMultiLocalOptionsActivity.findViewById(R.id.multiOptionsContinueBtn);
        assertNotNull(vNineTextView);
        assertNotNull(vToguzTextView);
        assertNotNull(vKorgoolTextView);
        assertNotNull(vNewGameBtn);
        assertNotNull(vContinueBtn);
    }

    @Test
    public void verifyNewGameBtn() {
        onView(withId(R.id.multiOptionsNewGameBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.GamePlayActivity")),
                toPackage(PACKAGE_NAME),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_mode),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_mode_multi)),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_type),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_type_multi_local)),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_option),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_option_newgame))));
    }

    @Test
    public void verifyContinueBtn() {
        onView(withId(R.id.multiOptionsContinueBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.GamePlayActivity")),
                toPackage(PACKAGE_NAME),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_mode),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_mode_multi)),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_type),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_type_multi_local)),
                hasExtra(mMultiLocalOptionsActivity.getString(R.string.intent_key_option),
                        mMultiLocalOptionsActivity.getString(R.string.intent_val_option_continue))));
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
