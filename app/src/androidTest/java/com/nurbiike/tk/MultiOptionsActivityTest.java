package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.MultiOptionsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

public class MultiOptionsActivityTest {

    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<MultiOptionsActivity> mActivityTestRule = new ActivityTestRule<MultiOptionsActivity>(MultiOptionsActivity.class);

    private MultiOptionsActivity mMultiOptionsActivity = null;

    @Before
    public void setUp() throws Exception {
        mMultiOptionsActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vNineTextView = mMultiOptionsActivity.findViewById(R.id.nineTextView);
        View vToguzTextView = mMultiOptionsActivity.findViewById(R.id.toguzTextView);
        View vKorgoolTextView = mMultiOptionsActivity.findViewById(R.id.korgoolTextView);
        View vIntRandBtn = mMultiOptionsActivity.findViewById(R.id.intRandomBtn);
        View vIntSpecBtn = mMultiOptionsActivity.findViewById(R.id.intSpecBtn);
        View vLocalBtn = mMultiOptionsActivity.findViewById(R.id.localBtn);
        assertNotNull(vNineTextView);
        assertNotNull(vToguzTextView);
        assertNotNull(vKorgoolTextView);
        assertNotNull(vIntRandBtn);
        assertNotNull(vIntSpecBtn);
        assertNotNull(vLocalBtn);
    }

    @Test
    public void verifyIntRandomBtn() {
        onView(withId(R.id.intRandomBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.GamePlayActivity")),
                toPackage(PACKAGE_NAME),
                hasExtra(mMultiOptionsActivity.getString(R.string.intent_key_mode),
                        mMultiOptionsActivity.getString(R.string.intent_val_mode_multi)),
                hasExtra(mMultiOptionsActivity.getString(R.string.intent_key_type),
                        mMultiOptionsActivity.getString(R.string.intent_val_type_multi_int_rand))));
    }

    @Test
    public void verifyIntSpecBtn() {
        onView(withId(R.id.intSpecBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.MultiIntSpecOptionsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void verifyLocalBtn() {
        onView(withId(R.id.localBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.MultiLocalOptionsActivity")),
                toPackage(PACKAGE_NAME)));
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
