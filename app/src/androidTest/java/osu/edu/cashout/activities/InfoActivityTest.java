package osu.edu.cashout.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.v4.app.Fragment;

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
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class InfoActivityTest {
    @Rule
    public ActivityTestRule<InfoActivity> mActivity = new ActivityTestRule<>(InfoActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
    }

    //Test to make sure that "upc" is being passed as the key to an intent extra when the user clicks the create review button
    @Test
    public void testIntentExtraForReadReviews() {
        Espresso.onView(withId(R.id.read_reviews_button)).perform(click());
        intended(hasExtraWithKey("upc"));
    }

    @After
    public void teardown() throws Exception {
        Intents.release();
    }
}