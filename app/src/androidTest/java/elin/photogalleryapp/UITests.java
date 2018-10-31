package elin.photogalleryapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestDates() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_fromDate)).perform(typeText("2018-09-18"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("2018-11-25"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestDates2() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_fromDate)).perform(typeText("2019-09-18"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("2019-11-25"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestCaption() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_caption)).perform(typeText("Hi"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestCaption2() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_caption)).perform(typeText("Bye"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestLocation() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_latitude)).perform(typeText("49.24"), closeSoftKeyboard());
        onView(withId(R.id.search_longitude)).perform(typeText("-123"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestLocation2() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_latitude)).perform(typeText("1"), closeSoftKeyboard());
        onView(withId(R.id.search_longitude)).perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    @Test
    public void TestAll() {
        onView(withId(R.id.btnFilter)).perform(click());
        onView(withId(R.id.search_fromDate)).perform(typeText("2018-09-18"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("2018-11-25"), closeSoftKeyboard());
        onView(withId(R.id.search_caption)).perform(typeText("Goodbye"), closeSoftKeyboard());
        onView(withId(R.id.search_latitude)).perform(typeText("49.24"), closeSoftKeyboard());
        onView(withId(R.id.search_longitude)).perform(typeText("-123"), closeSoftKeyboard());
        onView(withId(R.id.search_search)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }
}











