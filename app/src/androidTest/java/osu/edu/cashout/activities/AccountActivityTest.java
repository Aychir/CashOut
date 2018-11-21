package osu.edu.cashout.activities;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;
import osu.edu.cashout.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class AccountActivityTest {
    @Rule
    public ActivityTestRule<AccountActivity> mActivity = new ActivityTestRule<>(AccountActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
    }

    //Test to make sure that when the signout button is clicked an intent for the login activity is launched and that the login input views are displayed
    @Test
    public void testSignoutButtonFromAccount() {
        Espresso.onView(withId(R.id.signout_button)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
        Espresso.onView(withId(R.id.email)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.password)).check(matches(isDisplayed()));
    }

    @After
    public void teardown() throws Exception {
        Intents.release();
    }
}