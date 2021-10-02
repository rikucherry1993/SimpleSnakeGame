package com.rikucherry.simplesnake

import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rikucherry.simplesnake.views.GameView
import com.rikucherry.simplesnake.views.SquareFrameLayout
import com.rikucherry.simplesnake.views.SquareLinearLayout
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SnakeGameActivityTest {

    private lateinit var scenario: ActivityScenario<SnakeGameActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(SnakeGameActivity::class.java)
    }

    @Test
    fun initial_screen_with_correct_views() {

        onView(withId(R.id.game_toolbar)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.score_text)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.score_text)).check(ViewAssertions.matches(withText("0")))
        onView(withId(R.id.score_label)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.score_best_text)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.score_best_text)).check(ViewAssertions.matches(withText("0")))
        onView(withId(R.id.score_best_label)).check(ViewAssertions.matches(isDisplayed()))

        onView(withId(R.id.restart_button)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.game_frame)).check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun custom_square_views_are_real_squares() {
        scenario.onActivity {
            val frame = it.findViewById<SquareFrameLayout>(R.id.game_frame)
            val gameView = it.findViewById<GameView>(R.id.game_view)
            val buttonsView = it.findViewById<SquareLinearLayout>(R.id.direction_buttons)

            assert(frame.measuredWidth == frame.measuredHeight)
            assert(gameView.measuredWidth == gameView.measuredHeight)
            assert(buttonsView.measuredWidth == buttonsView.measuredHeight)
        }
    }

    @Test
    fun click_restart_will_reset_current_score() {
        onView(withId(R.id.score_text)).perform(setTextInTextView("100"))
        onView(withId(R.id.score_text)).check(ViewAssertions.matches(withText("100")))

        onView(withId(R.id.restart_button)).perform(click())
        onView(withId(R.id.score_text)).check(ViewAssertions.matches(withText("0")))
    }

    fun setTextInTextView(value: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(ViewMatchers.isDisplayed(), ViewMatchers.isAssignableFrom(
                    TextView::class.java))
            }

            override fun perform(uiController: UiController, view: View) {
                (view as TextView).text = value
            }

            override fun getDescription(): String {
                return "replace text"
            }
        }
    }
}