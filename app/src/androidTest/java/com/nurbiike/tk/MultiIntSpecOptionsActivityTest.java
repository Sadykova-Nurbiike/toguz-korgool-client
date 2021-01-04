package com.nurbiike.tk;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.nurbiike.tk.ui.MultiIntSpecOptionsActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class MultiIntSpecOptionsActivityTest {
    private static final String PACKAGE_NAME = "com.nurbiike.tk";

    @Rule
    public ActivityTestRule<MultiIntSpecOptionsActivity> mActivityTestRule = new ActivityTestRule<MultiIntSpecOptionsActivity>(MultiIntSpecOptionsActivity.class);

    private MultiIntSpecOptionsActivity mMultiIntSpecOptionsActivity = null;

    @Before
    public void setUp() throws Exception {
        mMultiIntSpecOptionsActivity = mActivityTestRule.getActivity();
        Intents.init();
    }

    @Test
    public void testLaunch() {
        View vNineTextView = mMultiIntSpecOptionsActivity.findViewById(R.id.nineTextView);
        View vToguzTextView = mMultiIntSpecOptionsActivity.findViewById(R.id.toguzTextView);
        View vKorgoolTextView = mMultiIntSpecOptionsActivity.findViewById(R.id.korgoolTextView);
        View vCreateBtn = mMultiIntSpecOptionsActivity.findViewById(R.id.createBtn);
        View vJoinBtn = mMultiIntSpecOptionsActivity.findViewById(R.id.joinBtn);
        assertNotNull(vNineTextView);
        assertNotNull(vToguzTextView);
        assertNotNull(vKorgoolTextView);
        assertNotNull(vCreateBtn);
        assertNotNull(vJoinBtn);
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
