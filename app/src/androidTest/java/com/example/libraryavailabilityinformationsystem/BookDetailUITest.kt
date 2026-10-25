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
                    showStatusAndReturnDate = true,
                    onBack = {},
                    onToggleSaved = {},
                    onToggleAlert = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Introduction to Algorithms", substring = true, useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Thomas H. Cormen", substring = true, useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Available now", substring = true, useUnmergedTree = true).onFirst().assertExists()
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
                    showStatusAndReturnDate = true,
                    onBack = {},
                    onToggleSaved = {},
                    onToggleAlert = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Clean Code", substring = true, useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("Currently borrowed", substring = true, useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText("EXPECTED RETURN DATE", substring = true, useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodesWithText(borrowedBook.returnDate!!, substring = true, useUnmergedTree = true).onFirst().assertExists()
    }
}
