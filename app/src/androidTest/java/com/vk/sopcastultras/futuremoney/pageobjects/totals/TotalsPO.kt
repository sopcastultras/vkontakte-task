package com.vk.sopcastultras.futuremoney.pageobjects.totals

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import com.atdroid.atyurin.futuremoney.R
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.extensions.textContains
import com.vk.sopcastultras.futuremoney.BasePage
import com.vk.sopcastultras.futuremoney.myStep
import com.vk.sopcastultras.futuremoney.pageelement.Calendar
import com.vk.sopcastultras.futuremoney.pageelement.Spinner.selectInSpinner
import com.vk.sopcastultras.futuremoney.withIndex
import org.joda.time.LocalDate

enum class TotalBeginTypes {
    ALL_TIME, SPECIFIC_DAY
}

object TotalsPO : BasePage<TotalsPO>() {
    private val accountsSum = withId(R.id.tv_accounts_total_value)
    private val incomesSum = withId(R.id.tv_incomes_total_value)
    private val outcomesSum = withId(R.id.tv_outcomes_total_value)
    private val totalSum = withId(R.id.tv_total_value)

    private val calculateTotalsButton = withId(R.id.action_btn_calc_totals)

    private val dateTypeSpinner = withId(R.id.sp_begin_date_type)

    fun calculate() {
        myStep("Нажать на 'Посчитать итоги'") {
            calculateTotalsButton.click()
        }
    }

    fun selectPeriod(beginTypes: TotalBeginTypes) {
        myStep("Выбираем тип периода расчета '$beginTypes'") {
            when (beginTypes) {
                TotalBeginTypes.ALL_TIME -> dateTypeSpinner.selectInSpinner("За все время")
                TotalBeginTypes.SPECIFIC_DAY -> dateTypeSpinner.selectInSpinner("С заданной даты")
            }
        }
    }

    fun selectDate(selectedDate: LocalDate, datePickerNumber: Int) {
        myStep("Выбираем расчетную дату '$selectedDate' для календаря №$datePickerNumber") {
            val matcher = withIndex(withSubstring(selectedDate.year.toString()), datePickerNumber)
            Calendar.selectDate(matcher, selectedDate)
        }
    }

    fun checkAccountsTotal(expected: String) {
        myStep("Ожидаем итоги для счетов '$expected'") {
            accountsSum.textContains(expected)
        }
    }

    fun checkIncomesTotal(expected: String) {
        myStep("Ожидаем итоги для доходов '$expected'") {
            incomesSum.textContains(expected)
        }
    }

    fun checkOutcomesTotal(expected: String) {
        myStep("Ожидаем итоги для расходов '$expected'") {
            outcomesSum.textContains(expected)
        }
    }

    fun checkGeneralTotal(expected: String) {
        myStep("Ожидаем общие итоги '$expected'") {
            totalSum.textContains(expected)
        }
    }

}
