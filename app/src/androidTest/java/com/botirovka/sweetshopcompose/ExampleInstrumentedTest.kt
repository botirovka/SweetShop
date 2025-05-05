package com.botirovka.sweetshopcompose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class UiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appLaunchesAndDisplaysSplashScreen() {
        composeTestRule
            .onNodeWithText("Sweet Shop")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Get Started")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testLoginScreenContent() {
        composeTestRule.onNodeWithText("Get Started").performClick()

        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter your email and password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forgot Password?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Don't have an account? ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Signup").assertIsDisplayed()
    }

    @Test
    fun testLogin() {
        composeTestRule.onNodeWithText("Get Started").performClick()

        composeTestRule.onNodeWithText("Email").performTextInput("tesst@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("12345678")

        composeTestRule.onNodeWithText("Log In").performClick()

        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Explore")
            .assertIsDisplayed()
    }

    @Test
    fun testSignUpScreenContent() {
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Signup").performClick()

        composeTestRule.onNodeWithText("Enter your email and password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
    }

    @Test
    fun testSignUpAndCheckBottomMenu() {
        composeTestRule.onNodeWithText("Get Started").performClick()
        composeTestRule.onNodeWithText("Signup").performClick()

        composeTestRule.onNodeWithText("Email").performTextInput("test333@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("12345678")

        composeTestRule.onNodeWithText("Sign Up").performClick()

        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Explore")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Cart").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Favourite").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Account").performClick()
        Thread.sleep(1000)
        composeTestRule.onNodeWithText("Logout").performClick()
    }
}
