package osu.edu.cashout.activities;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;
import osu.edu.cashout.R;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.times;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ManualSearchActivityTest {
    //Custom matcher to determine if there is an error on an EditText view
    public static Matcher<View> noInputError(){
        return new BoundedMatcher<View, EditText>(EditText.class){
            @Override
            public void describeTo(Description desc){
                desc.appendText("No error: ");
            }

            @Override
            protected boolean matchesSafely(EditText view){
                return view.getError() == null;
            }
        };
    }

    @Rule
    public ActivityTestRule<ManualSearchActivity> mActivity = new ActivityTestRule<>(ManualSearchActivity.class);

    @Before
    public void setup(){
        Intents.init();
    }

    //Test to make sure the input field has an error if there is no input and that no intent for info activity is launched
    @Test
    public void testEmptyInput(){
        Espresso.onView(withId(R.id.input_upc)).perform(replaceText(""));
        Espresso.onView(withId(R.id.manual_search_button)).perform(click());
        Espresso.onView(withId(R.id.input_upc)).check(matches(hasErrorText(mActivity.getActivity().getApplicationContext().getString(R.string.must_enter_code))));
        intended(hasComponent(InfoActivity.class.getName()), times(0));
    }

    //Test to make sure that when a upc is not found there is no error on the input and no intent for info activity is launched
    @Test
    public void testNonEmptyInput_UPCNotFound(){
        Espresso.onView(withId(R.id.input_upc)).perform(replaceText("a"));
        Espresso.onView(withId(R.id.manual_search_button)).perform(click());
        Espresso.onView(withId(R.id.input_upc)).check(matches(noInputError()));
        intended(hasComponent(InfoActivity.class.getName()), times(0));
    }

    @After
    public void teardown(){
        Intents.release();
    }

}