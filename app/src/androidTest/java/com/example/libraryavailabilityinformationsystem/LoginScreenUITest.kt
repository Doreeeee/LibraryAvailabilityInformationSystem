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

        composeTestRule.onAllNodesWithText("Sign In").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Username or Email").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Password").onFirst().assertIsDisplayed()
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

        composeTestRule.onAllNodesWithText("Username or Email").onFirst().performTextInput("Doe")
        composeTestRule.onAllNodesWithText("Password").onFirst().performTextInput("123")

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()

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

        composeTestRule.onAllNodesWithText("Username or Email").onFirst().performTextInput("InvalidUser")
        composeTestRule.onAllNodesWithText("Password").onFirst().performTextInput("wrongpass")

        composeTestRule.onNode(hasText("Sign In") and hasClickAction()).performClick()

        composeTestRule.onAllNodesWithText("Invalid credentials.", substring = true).onFirst().assertIsDisplayed()
    }
}
