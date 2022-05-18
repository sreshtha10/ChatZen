package com.sreshtha.chatappandroid.util

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class UtilityTest {

    @Test
    fun validateRegistrationInputWhenEmailIsEmptyReturnsFalse() {
        val result = Utility.validateRegistrationInput(
            email = "",
            password = "abcd@1242",
            confirmedPassword = "abcd@12423"
        )
        assertThat(result).isFalse()
    }


    @Test
    fun validateRegistrationInputWhenPasswordIsEmptyReturnsFalse(){
        val result = Utility.validateRegistrationInput(
            email = "sreshtha.mehrotra@gmail.com",
            password = "",
            confirmedPassword = "abcd@12423"
        )
        assertThat(result).isFalse()
    }

    @Test
    fun validateRegistrationInputWhenConfirmPasswordIsEmptyReturnsFalse(){
        val result = Utility.validateRegistrationInput(
            email = "sreshtha.11121@gmail.com",
            password = "abcd@122",
            confirmedPassword = ""
        )
        assertThat(result).isFalse()
    }


    @Test
    fun validateRegistrationInputReturnsTrue(){
        val result = Utility.validateRegistrationInput(
            email = "sreshtha.mehrotra@gmail.com",
            password = "abcd@1232",
            confirmedPassword = "abcd@12423"
        )
        assertThat(result).isTrue()
    }


    @Test
    fun validateLoginInputWhenEmailIsEmptyReturnsFalse() {
        val result = Utility.validateLoginInput(
            email = "",
            password = "abcd@1"
        )

        assertThat(result).isFalse()
    }


    @Test
    fun validateLoginInputWhenPasswordIsEmptyReturnsFalse(){
        val result = Utility.validateLoginInput(
            email = "sreshtha.mehrotra@gmail.com",
            password = ""
        )

        assertThat(result).isFalse()
    }


    @Test
    fun validateLoginInputReturnsTrue(){
        val result = Utility.validateLoginInput(
            email = "sreshtha.mehrotra@gmail.com",
            password = "abcd@123"
        )

        assertThat(result).isTrue()
    }


    @Test
    fun isValidEmailAtTheRateSymbolIsNotPresentReturnsFalse() {
        val result = Utility.isValidEmail(
            email = "sreshtha.mehrotra.gmail.com"
        )

        assertThat(result).isFalse()

    }



    @Test
    fun isValidEmailWhenEmptyAfterAtTheRateSymbolReturnsFalse(){
        val result = Utility.isValidEmail(
            email = "sreshtha.mehrotra@"
        )

        assertThat(result).isFalse()
    }

    @Test
    fun isValidEmailWhenDotIsNotPresentInTheSecondPartReturnsFalse(){
        val result = Utility.isValidEmail(
            email = "sreshtha.mehrotra@gmail"
        )

        assertThat(result).isFalse()
    }

    @Test
    fun isValidEmailWhenFirstPartIsEmptyReturnsFalse(){
        val result = Utility.isValidEmail(
            email = "@gmail.com"
        )

        assertThat(result).isFalse()
    }



    @Test
    fun isValidEmailReturnsTrue(){
        val result1 = Utility.isValidEmail(
            email = "sreshtha.mehrotra@gmail.com"
        )

        val result2 = Utility.isValidEmail(
            email = "sreshtha._19939@gmail.com"
        )

        val result3 = Utility.isValidEmail(
            email = "199392@yahoo.co.in"
        )
        assertThat(result1).isTrue()
        assertThat(result2).isTrue()
        assertThat(result3).isTrue()
    }

    @Test
    fun passwordIsSameAsConfirmPasswordReturnsFalse() {
        val result = Utility.passwordIsSameAsConfirmPassword(
            password = "aA@1_1",
            confirmedPassword = "aA@1bcd"
        )

        assertThat(result).isFalse()
    }

    @Test
    fun passwordIsSameAsConfirmPasswordReturnsTrue() {
        val result = Utility.passwordIsSameAsConfirmPassword(
            password = "aA@1bcd",
            confirmedPassword = "aA@1bcd"
        )
        assertThat(result).isTrue()
    }
}