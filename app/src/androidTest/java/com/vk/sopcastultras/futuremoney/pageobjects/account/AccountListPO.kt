package com.vk.sopcastultras.futuremoney.pageobjects.account

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.atdroid.atyurin.futuremoney.R
import com.vk.sopcastultras.futuremoney.BasePage
import com.vk.sopcastultras.futuremoney.click

object AccountListPO : BasePage<AccountListPO>() {
    private val createButton = withId(R.id.fab)

    fun createAccount() = createButton.click()

    fun openAccount(accountName: String) = withText(accountName).click()
}