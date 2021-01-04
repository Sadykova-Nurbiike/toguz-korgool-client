package com.nurbiike.tk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameStateMultiPlayerTest.class,
        GameStateSingleTest.class
})
public class UnitTestSuite {
}