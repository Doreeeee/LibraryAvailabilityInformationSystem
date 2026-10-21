package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookSearchUITest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToBookSearch() {
        composeTestRule.waitForIdle()

        // 1. If on Welcome or Detail screen, go back if needed
        val backButtons = composeTestRule.onAllNodes(hasContentDescription("Back to sign in") or hasContentDescription("Go back"))
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

        // 3. Login as Doe if on Login screen
        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        if (textInputs.fetchSemanticsNodes().size >= 2) {
            textInputs[0].performTextClearance()
            textInputs[0].performTextInput("Doe")
            textInputs[1].performTextClearance()
            textInputs[1].performTextInput("123")

            composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()
            composeTestRule.waitForIdle()
        }

        // 4. Open Search for books if on Welcome screen
        val searchOptions = composeTestRule.onAllNodesWithText("Search for books", substring = true)
        if (searchOptions.fetchSemanticsNodes().isNotEmpty()) {
            searchOptions.onFirst().performClick()
            composeTestRule.waitForIdle()
        }
    }

    @Test
    fun testBookSearchScreen_rendersSearchInputAndBooks() {
        navigateToBookSearch()

        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Clean Code", substring = true).onFirst().assertIsDisplayed()
    }

    @Test
    fun testBookSearch_filteringByTitle() {
        navigateToBookSearch()

        // Perform search input query "Algorithms"
        val searchInput = composeTestRule.onAllNodes(hasSetTextAction()).onFirst()
        searchInput.performTextClearance()
        searchInput.performTextInput("Algorithms")
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true).onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Clean Code", substring = true).assertCountEquals(0)
    }
}
