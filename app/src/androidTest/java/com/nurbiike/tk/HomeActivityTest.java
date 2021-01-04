package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.HomeActivity;

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
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    //to test an activity we use @Rule
    //ActivityTestRule enables lunching of the activity
    //Note: ActivityTestRule was not imported automatically by the IDE. Steps needed:
    //  added       androidTestImplementation 'com.android.support.test:rules:1.0.2'    to app/build.gradle
    //  added       import androidx.test.rule.ActivityTestRule;     to this file
    //  synced the project with the gradle files by clicking the sync button
    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<HomeActivity>(HomeActivity.class);

    //a reference to HomeActivity
    private HomeActivity mHomeActivity = null;

    //this is used to set the necessary preconditions prior to execution of @Test
    @Before
    public void setUp() throws Exception {
        mHomeActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vNineTextView = mHomeActivity.findViewById(R.id.nineTextView);
        View vToguzTextView = mHomeActivity.findViewById(R.id.toguzTextView);
        View vKorgoolTextView = mHomeActivity.findViewById(R.id.korgoolTextView);
        View vSinglePBtn = mHomeActivity.findViewById(R.id.singlePBtn);
        View vMultiPBtn = mHomeActivity.findViewById(R.id.multiPBtn);
        View vSettingsBtn = mHomeActivity.findViewById(R.id.settingsBtn);
        assertNotNull(vNineTextView);
        assertNotNull(vToguzTextView);
        assertNotNull(vKorgoolTextView);
        assertNotNull(vSinglePBtn);
        assertNotNull(vMultiPBtn);
        assertNotNull(vSettingsBtn);
    }

    @Test
    public void verifySinglePBtn() {
        // Click Single Player button to intent to SingleOptionsActivity
        onView(withId(R.id.singlePBtn)).perform(click());

        // Verifies that the SingleOptionsActivity received an intent
        // with the correct package
        intended(allOf(
                hasComponent(hasShortClassName(".ui.SingleOptionsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void verifyMultiPBtn() {
        onView(withId(R.id.multiPBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.MultiOptionsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void verifySettingsBtn() {
        onView(withId(R.id.settingsBtn)).perform(click());

        intended(allOf(
                hasComponent(hasShortClassName(".ui.SettingsActivity")),
                toPackage(PACKAGE_NAME)));
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.nurbiike.tk", appContext.getPackageName());
    }

    //this is used to cleanup after the execution of @Test
    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}
