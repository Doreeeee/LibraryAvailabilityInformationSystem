package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.ui.DetailScreen
import com.example.libraryavailabilityinformationsystem.ui.theme.LibraryAvailabilityInformationSystemTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDetailUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBookDetailScreen_displaysTitleAuthorAndStatus() {
        val book = LaisRepository.initialBooks.first() // Introduction to Algorithms

        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                DetailScreen(
                    book = book,
                    saved = false,
                    alert = false,
                    onBack = {},
                    onToggleSaved = {},
                    onToggleAlert = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Introduction to Algorithms").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Thomas H. Cormen · Year 2022").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Available now").onFirst().assertIsDisplayed()
    }

    @Test
    fun testBookDetailScreen_borrowedBook_displaysExpectedReturnDate() {
        val borrowedBook = LaisRepository.initialBooks.first { it.returnDate != null } // Clean Code

        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                DetailScreen(
                    book = borrowedBook,
                    saved = false,
                    alert = false,
                    onBack = {},
                    onToggleSaved = {},
                    onToggleAlert = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Clean Code").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Currently borrowed").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("EXPECTED RETURN DATE & TIME").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("24 May 2025").onFirst().assertIsDisplayed()
    }
}
