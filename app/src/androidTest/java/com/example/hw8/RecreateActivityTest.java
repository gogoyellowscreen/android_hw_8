package com.example.hw8;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class RecreateActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void recreateActivitySamePosts() {
        onView(withId(R.id.reset)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.myRecyclerView)).check(new RecyclerViewItemCountAssertion(101));

        mainActivity.getScenario().recreate();

        onView(withId(R.id.myRecyclerView)).check(new RecyclerViewItemCountAssertion(101));
    }
}
