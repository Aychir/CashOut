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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertNull;

public class ScanActivityTest {
    @Rule
    public ActivityTestRule<ScanActivity> mActivity = new ActivityTestRule<>(ScanActivity.class);

    @Before
    public void setup() throws Exception {
        Intents.init();
    }

    //Test to make sure the account button on the scan screen launches an intent for the account activity
    @Test
    public void testAccountButtonFromScan() {
        Espresso.onView(withId(R.id.account_button)).perform(click());
        intended(hasComponent(AccountActivity.class.getName()));
    }

    @After
    public void teardown() throws Exception {
        Intents.release();
    }
}