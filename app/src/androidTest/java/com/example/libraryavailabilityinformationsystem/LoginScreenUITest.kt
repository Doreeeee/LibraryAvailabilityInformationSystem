package com.example.libraryavailabilityinformationsystem

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.libraryavailabilityinformationsystem.data.LaisRepository
import com.example.libraryavailabilityinformationsystem.ui.LoginRegisterScreen
import com.example.libraryavailabilityinformationsystem.ui.theme.LibraryAvailabilityInformationSystemTheme
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginScreen_rendersSignInTitleAndInputs() {
        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                LoginRegisterScreen(
                    usersList = LaisRepository.initialUsers,
                    onBack = {},
                    onAuthSuccess = {},
                    onRegisterUser = {}
                )
            }
        }

        composeTestRule.onAllNodesWithText("Sign In", useUnmergedTree = true).onFirst().assertExists()
        composeTestRule.onAllNodes(hasSetTextAction()).assertCountEquals(2)
        composeTestRule.onNodeWithText("Register Account", substring = true).assertExists()
    }

    @Test
    fun testLoginScreen_validCredentials_triggersSuccess() {
        var authenticatedUser: String? = null

        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                LoginRegisterScreen(
                    usersList = LaisRepository.initialUsers,
                    onBack = {},
                    onAuthSuccess = { user -> authenticatedUser = user.username },
                    onRegisterUser = {}
                )
            }
        }

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        textInputs[0].performTextInput("doe@univ.edu")
        textInputs[1].performTextInput("123")

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()

        composeTestRule.waitUntil(15000) {
            authenticatedUser != null
        }

        assertNotNull(authenticatedUser)
    }

    @Test
    fun testLoginScreen_invalidCredentials_showsError() {
        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                LoginRegisterScreen(
                    usersList = LaisRepository.initialUsers,
                    onBack = {},
                    onAuthSuccess = {},
                    onRegisterUser = {}
                )
            }
        }

        val textInputs = composeTestRule.onAllNodes(hasSetTextAction())
        textInputs[0].performTextInput("wrong@univ.edu")
        textInputs[1].performTextInput("WrongPassword")

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()

        composeTestRule.waitUntil(15000) {
            composeTestRule.onAllNodesWithText("Invalid credentials", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Invalid credentials", substring = true).assertIsDisplayed()
    }

    @Test
    fun testLoginScreen_emptyFields_showValidationError() {
        composeTestRule.setContent {
            LibraryAvailabilityInformationSystemTheme {
                LoginRegisterScreen(
                    usersList = LaisRepository.initialUsers,
                    onBack = {},
                    onAuthSuccess = {},
                    onRegisterUser = {}
                )
            }
        }

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).assertIsNotEnabled()
    }
}
