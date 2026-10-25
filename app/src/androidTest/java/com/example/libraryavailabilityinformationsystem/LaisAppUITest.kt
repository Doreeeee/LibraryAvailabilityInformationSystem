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

        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithText("Sign In", useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
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
            textInputs[0].performTextInput("doe@univ.edu")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("123")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithText("WELCOME, DOE", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("WELCOME, DOE", substring = true).assertIsDisplayed()
    }
}
