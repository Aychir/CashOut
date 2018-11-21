package osu.edu.cashout.activities;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import osu.edu.cashout.R;

import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivity = new ActivityTestRule<>(LoginActivity.class);

    //Test to make sure the email and password views are visible and that they can be edited
    @Test
    public void testEditTextFields() {
        Espresso.onView(withId(R.id.email)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.password)).check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.email)).perform(replaceText("test_email@gmail.com"));
        Espresso.onView(withId(R.id.password)).perform(replaceText("testpassword123"));
        Espresso.onView(withId(R.id.email)).check(matches(withText("test_email@gmail.com")));
        Espresso.onView(withId(R.id.password)).check(matches(withText("testpassword123")));
    }

}