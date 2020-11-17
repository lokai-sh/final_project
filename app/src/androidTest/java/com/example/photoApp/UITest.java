package com.example.photoApp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.photoApp.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void searchForAnImageInGivenTimeRangeUsingCaptionFilter(){
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-11-15"), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(typeText("2020-11-16"), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText("Sofa"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("Sofa")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("210348"))));

    }

    @Test
    public void testSwipeLeft(){

        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.etCaption)).check(matches(withText("Cat")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("212750"))));
//        onView(withId(R.id.etFromDateTime)).perform(clearText());
//        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-09-30"), closeSoftKeyboard());
//        onView(withId(R.id.etToDateTime)).perform(clearText());
//        onView(withId(R.id.etToDateTime)).perform(typeText("2020-10-03"), closeSoftKeyboard());
//        onView(withId(R.id.etKeywords)).perform(typeText("door"), closeSoftKeyboard());
//        onView(withId(R.id.go)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("door")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("215945"))));
//        onView(withId(R.id.btnNext)).perform(click());
//        onView(withId(R.id.etCaption)).check(matches(withText("kitchen door")));
//        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("191228"))));
    }


}

