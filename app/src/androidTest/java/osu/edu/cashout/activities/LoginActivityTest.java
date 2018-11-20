package osu.edu.cashout.activities;

import android.app.Activity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import osu.edu.cashout.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class LoginActivityTest{
    @Rule
    public ActivityTestRule<LoginActivity> mActivity = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
    }

    //Test to make sure the signup button on the login screen launches an intent for the signup activity
    @Test
    public void testSignupButton() {
        Espresso.onView(withId(R.id.signup_button)).perform(click());
        intended(hasComponent(SignupActivity.class.getName()));
    }

    @After
    public void teardown() throws Exception {
        Intents.release();
    }
}