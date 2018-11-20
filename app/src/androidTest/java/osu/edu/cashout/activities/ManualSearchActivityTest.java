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
import static org.junit.Assert.*;

public class ManualSearchActivityTest {
    @Rule
    public ActivityTestRule<ManualSearchActivity> mActivity = new ActivityTestRule<>(ManualSearchActivity.class);

    //Test to make sure the input field has an error if there is no input
    @Test
    public void checkEmptyInput(){
        Espresso.onView(withId(R.id.input_upc)).perform(replaceText(""));
        Espresso.onView(withId(R.id.manual_search_button)).perform(click());
        Espresso.onView(withId(R.id.input_upc)).check(matches(hasErrorText(mActivity.getActivity().getApplicationContext().getString(R.string.must_enter_code))));
    }

}