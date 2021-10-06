package com.creativethoughts.iscore;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by vishnu on 7/17/2017 - 11:42 AM.
 */
@RunWith(AndroidJUnit4.class)
public class UserRegistrationTestingClass {
    @Rule
    public ActivityTestRule<UserRegistrationActivity> mActivityRule =
            new ActivityTestRule<>(UserRegistrationActivity.class);
    @Test
    public void mScoreTest(){

    }
}
