package com.nurbiike.tk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        HomeActivityTest.class,
        MultiIntSpecOptionsActivityTest.class,
        MultiOptionsActivityTest.class,
        SingleOptionsActivityTest.class,
        MultiLocalOptionsActivityTest.class,
        SettingsActivityTest.class,
        GamePlayActivityTest.class
})
public class InstrumentedTestSuite {
}