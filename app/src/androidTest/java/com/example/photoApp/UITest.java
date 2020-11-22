package com.example.photoApp;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.example.photoApp.presenter.MainActivityPresenter;
import com.example.photoApp.view.MainActivity;
import com.google.android.material.internal.NavigationMenuItemView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;


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
    public void testSwipePrevious_NoPreviousPhoto(){
        //Select Previous (Should stay on Cat as this is the first photo)
        onView(withId(R.id.ivGallery)).perform(swipeRight());
        onView(withId(R.id.etCaption)).check(matches(withText("Cat"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("164720")))); //Update to details from first picture
    }

    @Test
    public void testSwipeNext_NoLastPhoto(){
        //Select Previous (Should stay on Cat as this is the first photo)
        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.ivGallery)).perform(swipeLeft());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710")))); //Update to details from first picture
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

    @Test
    public void testScrollPrevious_NoPreviousPhoto(){
        //Select Previous (Should stay on Cat as this is the first photo)
        onView(withId(R.id.btnPrev)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("Cat"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("164720")))); //Update to details from first picture
    }

    @Test
    public void testScrollNext_NoLastPhoto(){
        //Select Previous (Should stay on Cat as this is the first photo)
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa"))); //Update to details from first picture in list
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710")))); //Update to details from first picture
    }

    @Test
    public void searchForAnImageUsingUpperCaseCaptionFilter(){
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etKeywords)).perform(typeText("SOFA"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710"))));
    }

    @Test
    public void searchForAnImageUsingLowerCaseCaptionFilter(){
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etKeywords)).perform(typeText("sofa"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710"))));
    }

    @Test
    public void searchForAnImageUsingProperCaseCaptionFilter(){
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etKeywords)).perform(typeText("Sofa"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("sofa")));
        onView(withId(R.id.tvTimestamp)).check(matches(withText(containsString("213710"))));
    }

 /*   @Test
    public void removeAnImage(){
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(clearText());
        onView(withId(R.id.etToDateTime)).perform(clearText());
        onView(withId(R.id.etKeywords)).perform(typeText("Dog"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("dog")));
        onView(withId(R.id.btnRemove)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("cat")));
        onView(withId(R.id.btnNext)).check(matches(withText("door")));
    }*/

//Tests for Navigation Drawer
    @Test
    public void testNavigationDrawer_Upload() {
      //  onView(withId(R.id.navigation_upload)).perform(click());
        //Todo: How do we test that we are on the Upload screen no control to use to validate such as a button?
    }

    @Test
    public void testNavigationDrawer_Camera() {
      //  onView(withId(R.id.navigation_camera)).perform(click());
        //Todo: How do we test that we are on the Camera screen no control to use to validate such as a button?
    }

    @Test
    public void testNavigationDrawer_Home() {
        onView(withId(R.id.navigation_home)).perform(click());
        //Check that the btnNext is visible
        onView(withId(R.id.btnNext)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationDrawer_Search() {
        onView(withId(R.id.navigation_search)).perform(click());
        //Check that the Edit Text Keywords is visible
        onView(withId(R.id.etKeywords)).check(matches(isDisplayed()));
        onView(withId(R.id.btnCancel)).perform(click());
    }
}

