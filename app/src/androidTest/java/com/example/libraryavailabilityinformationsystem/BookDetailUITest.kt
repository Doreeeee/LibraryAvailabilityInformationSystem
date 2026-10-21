package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDetailUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToBookSearch() {
        composeTestRule.waitForIdle()

        // 1. If on Detail screen, go back
        val backButtons = composeTestRule.onAllNodes(hasContentDescription("Go back") or hasContentDescription("Back to sign in"))
        if (backButtons.fetchSemanticsNodes().isNotEmpty()) {
            backButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        // 2. Splash -> Login
        val loginButtons = composeTestRule.onAllNodes(hasText("Login") and hasClickAction())
        if (loginButtons.fetchSemanticsNodes().isNotEmpty()) {
            loginButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        // 3. Login as Doe
        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        if (textInputs.fetchSemanticsNodes().size >= 2) {
            textInputs[0].performTextClearance()
            textInputs[0].performTextInput("Doe")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("123")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        // 4. Open Search for books
        val searchOptions = composeTestRule.onAllNodesWithText("Search for books", substring = true)
        if (searchOptions.fetchSemanticsNodes().isNotEmpty()) {
            searchOptions.onFirst().performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun testBookDetailScreen_displaysTitleAuthorAndStatus() {
        navigateToBookSearch()

        // Click on Introduction to Algorithms
        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Thomas H. Cormen", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Available now", substring = true).onFirst().assertIsDisplayed()
    }

    @Test
    fun testBookDetailScreen_borrowedBook_displaysExpectedReturnDate() {
        navigateToBookSearch()

        // Go back to book search if in DetailScreen
        val backButtons = composeTestRule.onAllNodes(hasContentDescription("Go back"))
        if (backButtons.fetchSemanticsNodes().isNotEmpty()) {
            backButtons.onFirst().performClick()
            composeTestRule.waitForIdle()
        }

        // Click on Clean Code (Borrowed Book)
        composeTestRule.onAllNodesWithText("Clean Code", substring = true).onFirst().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Clean Code", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Currently borrowed", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("EXPECTED RETURN DATE", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("24 May 2025", substring = true).onFirst().assertIsDisplayed()
    }
}
