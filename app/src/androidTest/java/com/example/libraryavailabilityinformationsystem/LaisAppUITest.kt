package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaisAppUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testSplashToLoginNavigation() {
        composeTestRule.waitForIdle()

        val splashTitle = composeTestRule.onAllNodesWithText("LIBRARY AVAILABILITY", substring = true)
        if (splashTitle.fetchSemanticsNodes().isNotEmpty()) {
            splashTitle.onFirst().assertExists()

            composeTestRule.onNode(hasText("Login") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).assertExists()
    }

    @Test
    fun testStudentLoginAndNavigation() {
        composeTestRule.waitForIdle()

        val loginButtons = composeTestRule.onAllNodes(hasText("Login") and hasClickAction())
        if (loginButtons.fetchSemanticsNodes().isNotEmpty()) {
            loginButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        if (textInputs.fetchSemanticsNodes().size >= 2) {
            textInputs[0].performTextClearance()
            textInputs[0].performTextInput("Doe")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("123")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithText("WELCOME, DOE", substring = true).assertIsDisplayed()
    }

    @Test
    fun testBookSearchAndDetailFlow() {
        composeTestRule.waitForIdle()

        val loginButtons = composeTestRule.onAllNodes(hasText("Login") and hasClickAction())
        if (loginButtons.fetchSemanticsNodes().isNotEmpty()) {
            loginButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        if (textInputs.fetchSemanticsNodes().size >= 2) {
            textInputs[0].performTextClearance()
            textInputs[0].performTextInput("Doe")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("123")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        val searchOptions = composeTestRule.onAllNodesWithText("Search for books", substring = true)
        if (searchOptions.fetchSemanticsNodes().isNotEmpty()) {
            searchOptions.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Thomas H. Cormen", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Available now", substring = true).onFirst().assertIsDisplayed()
    }

    @Test
    fun testInvalidLogin_showsErrorMessage() {
        composeTestRule.waitForIdle()

        val backButtons = composeTestRule.onAllNodes(hasContentDescription("Back to sign in") or hasContentDescription("Go back"))
        if (backButtons.fetchSemanticsNodes().isNotEmpty()) {
            backButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        val loginButtons = composeTestRule.onAllNodes(hasText("Login") and hasClickAction())
        if (loginButtons.fetchSemanticsNodes().isNotEmpty()) {
            loginButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        if (textInputs.fetchSemanticsNodes().size >= 2) {
            textInputs[0].performTextClearance()
            textInputs[0].performTextInput("InvalidUser")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("wrongpass")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.onNodeWithText("Invalid credentials", substring = true).assertIsDisplayed()
    }
}
