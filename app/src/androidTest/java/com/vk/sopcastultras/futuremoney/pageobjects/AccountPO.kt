package com.vk.sopcastultras.futuremoney.pageobjects

import androidx.test.espresso.matcher.ViewMatchers.withId
import com.atdroid.atyurin.futuremoney.R
import com.vk.sopcastultras.futuremoney.BasePage

object AccountPO : BasePage() {
    private val nameField = withId(R.id.et_account_name_value)
    private val amountField = withId(R.id.et_account_amount_value)
    private val saveButton = withId(R.id.action_btn_add_budget_item_confirm)

    fun enterName(text: String) = nameField.typeText(text)

    fun enterAmount(text: String) = amountField.typeText(text)

    fun save() = saveButton.click()

}
