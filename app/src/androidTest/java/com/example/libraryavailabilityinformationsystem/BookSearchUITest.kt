package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.models.HomeMode
import com.example.libraryavailabilityinformationsystem.ui.HomeScreen
import com.example.libraryavailabilityinformationsystem.ui.theme.LibraryAvailabilityInformationSystemTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookSearchUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBookSearchScreen_rendersSearchInputAndBooks() {
        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                HomeScreen(
                    mode = HomeMode.Search,
                    query = "",
                    onQueryChange = {},
                    category = "All",
                    onCategoryChange = {},
                    availabilityFilter = "All",
                    onAvailabilityChange = {},
                    savedIds = emptyList(),
                    allBooksList = LaisRepository.initialBooks,
                    categoriesList = LaisRepository.categories,
                    onToggleSaved = {},
                    onBookClick = {},
                    onTabSelected = {},
                    onReset = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Title, author or keyword").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Introduction to Algorithms").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Clean Code").onFirst().assertIsDisplayed()
    }

    @Test
    fun testBookSearch_filteringByTitle() {
        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                HomeScreen(
                    mode = HomeMode.Search,
                    query = "Algorithms",
                    onQueryChange = {},
                    category = "All",
                    onCategoryChange = {},
                    availabilityFilter = "All",
                    onAvailabilityChange = {},
                    savedIds = emptyList(),
                    allBooksList = LaisRepository.initialBooks,
                    categoriesList = LaisRepository.categories,
                    onToggleSaved = {},
                    onBookClick = {},
                    onTabSelected = {},
                    onReset = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Introduction to Algorithms").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Clean Code").assertCountEquals(0)
    }
}
