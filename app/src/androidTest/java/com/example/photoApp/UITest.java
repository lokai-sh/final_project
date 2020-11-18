package com.example.photoApp;

import androidx.test.espresso.action.ViewActions;
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
import static androidx.test.espresso.action.ViewActions.swipeRight;
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
        //onView(withId(R.id.etFromDateTime)).perform(typeText("2020-11-15"), closeSoftKeyboard());
        onView(withId(R.id.etFromDateTime)).perform(typeText("2020-10-04"), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(typeText("2020-11-17"), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText("sofa"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710"))));

    }

    @Test
    public void testSwipeFunctionality(){
        //Swipe Left
        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.etCaption)).check(matches(withText("dog"))); //Details for second picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("221336")))); //Details for second picture in list
        //Swipe Right
        onView(withId(R.id.ivGallery)).perform(swipeRight());
        onView(withId(R.id.etCaption)).check(matches(withText("Cat"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("164720")))); //Update to details from first picture
    }

    @Test
    public void testScrollPhotos(){
        //Select Next
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("dog"))); //Details for second picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("221336")))); //Details for second picture in list
        //Select Previous
        onView(withId(R.id.btnPrev)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("Cat"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("164720")))); //Update to details from first picture
    }
    
}

