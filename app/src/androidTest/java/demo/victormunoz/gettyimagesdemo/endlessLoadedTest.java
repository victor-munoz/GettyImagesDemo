package demo.victormunoz.gettyimagesdemo;

import android.support.design.widget.CoordinatorLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import demo.victormunoz.gettyimagesdemo.features.search.SearchActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static demo.victormunoz.gettyimagesdemo.endlessLoadedTest.Matchers.withItemCount;
import static demo.victormunoz.gettyimagesdemo.macher.RecyclerViewMatcher.atPosition;


@RunWith(AndroidJUnit4.class)
@MediumTest
public class endlessLoadedTest {
    @Rule
    public final ActivityTestRule<SearchActivity> mNotesActivityTestRule = new ActivityTestRule<>(SearchActivity.class);
    private int pageSize;

    @Before
    public void setUp(){
        //set number of images downloaded in one call
        pageSize = InstrumentationRegistry.getTargetContext().getResources().getInteger(R.integer.page_size);
        //set idle
        Espresso.registerIdlingResources(mNotesActivityTestRule.getActivity().getCountingIdlingResource());
        //trick to allow scrollToPosition inside CoordinatorLayout, otherwise the scroll will not be
        // performed
        mNotesActivityTestRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                RecyclerView recyclerView = mNotesActivityTestRule.getActivity().findViewById(R.id.recycler_view);
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) recyclerView.getLayoutParams();
                params.setBehavior(null);
                recyclerView.requestLayout();
            }
        });
    }

    /**
     * scroll four times to the last element of the recyclerview and the check the total number of
     * images downloaded
     */
    @Test
    public void endlessScrollingTest(){
        //search images with the phrase 'g'
        onView(withId(R.id.search)).perform(typeText("g"), pressImeActionButton());
        //scroll
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(pageSize - 1));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(pageSize * 2 - 1));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(pageSize * 3 - 1));
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(pageSize * 4 - 1));
        //check if item is displayed
        onView(withId(R.id.recycler_view)).check(matches(atPosition((pageSize * 4 - 1), isDisplayed())));
        //check total loaded images
        onView(withId(R.id.recycler_view)).check(matches(withItemCount(pageSize * 5)));

    }

    @After
    public void unregisterIdlingResource(){
        onView(withId(R.id.recycler_view)).perform(scrollToPosition(0));
        Espresso.unregisterIdlingResources(mNotesActivityTestRule.getActivity().getCountingIdlingResource());
    }

    static class Matchers {
        static Matcher<View> withItemCount(final int size){
            return new TypeSafeMatcher<View>() {
                @Override
                public boolean matchesSafely(final View view){
                    return ((RecyclerView) view).getAdapter().getItemCount() == size;
                }

                @Override
                public void describeTo(final Description description){
                    description.appendText("recyclerView should have " + size + " items");
                }
            };
        }
    }

}
