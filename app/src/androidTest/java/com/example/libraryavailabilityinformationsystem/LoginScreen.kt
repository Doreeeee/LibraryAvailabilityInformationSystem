package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToLoginScreen() {
        composeTestRule.waitForIdle()

        // 1. If on Welcome screen or detail screen, click back to return to Login
        val backToSignIn = composeTestRule.onAllNodes(hasContentDescription("Back to sign in") or hasContentDescription("Go back"))
        if (backToSignIn.fetchSemanticsNodes().isNotEmpty()) {
            backToSignIn.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        // 2. If on Splash screen, click Login button
        val loginButtons = composeTestRule.onAllNodes(hasText("Login") and hasClickAction())
        if (loginButtons.fetchSemanticsNodes().isNotEmpty()) {
            loginButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun loginScreen_displaysRequiredElements() {
        navigateToLoginScreen()

        // Check username & password fields exist
        composeTestRule.onAllNodes(hasSetTextAction()).assertCountEquals(2)

        // Check Sign In button exists
        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).assertExists()

        // Check Register Account link exists
        composeTestRule.onNodeWithText("Register Account", substring = true).assertExists()
    }

    @Test
    fun loginScreen_student_canLogin() {
        navigateToLoginScreen()

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())

        textInputs[0].performTextClearance()
        textInputs[0].performTextInput("Doe")

        textInputs[1].performTextClearance()
        textInputs[1].performTextInput("123")

        // Click Sign In button
        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Verify student welcome screen renders
        composeTestRule.onNodeWithText("WELCOME, DOE", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Search for books", substring = true).assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysErrorForInvalidCredentials() {
        navigateToLoginScreen()

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())

        textInputs[0].performTextClearance()
        textInputs[0].performTextInput("WrongUser")

        textInputs[1].performTextClearance()
        textInputs[1].performTextInput("WrongPassword")

        // Click Sign In button
        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
        composeTestRule.waitForIdle()

        // Verify error message is displayed
        composeTestRule.onNodeWithText("Invalid credentials", substring = true).assertIsDisplayed()
    }

    @Test
    fun loginScreen_emptyFields_showValidationError() {
        navigateToLoginScreen()

        // Clear text inputs if any
        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        textInputs[0].performTextClearance()
        textInputs[1].performTextClearance()

        // Verify Sign In button is disabled when inputs are empty
        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).assertIsNotEnabled()
    }
}
