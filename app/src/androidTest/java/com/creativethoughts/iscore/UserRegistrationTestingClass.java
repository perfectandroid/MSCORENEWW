package com.creativethoughts.iscore;



import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

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
