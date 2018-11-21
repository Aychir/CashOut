package osu.edu.cashout.activities;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import osu.edu.cashout.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class SignupActivityTest {
    @Rule
    public ActivityTestRule<SignupActivity> mActivity = new ActivityTestRule<>(SignupActivity.class);

    //Test to make sure an error is displayed if the passwords entered do not match
    @Test
    public void testSignup_PasswordMismatch() {
        Espresso.onView(withId(R.id.password)).perform(replaceText("1234567"));
        Espresso.onView(withId(R.id.password_confirmation)).perform(replaceText("123456"));
        Espresso.onView(withId(R.id.signup_button)).perform(click());
        Espresso.onView(withId(R.id.password_confirmation)).check(matches(hasErrorText(mActivity.getActivity().getApplicationContext().getString(R.string.unmatched_passwords))));
    }
}
