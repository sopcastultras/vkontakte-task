package com.vk.sopcastultras.futuremoney

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import com.atdroid.atyurin.futuremoney.activity.MainActivity
import io.qameta.allure.android.rules.LogcatRule
import io.qameta.allure.android.rules.WindowHierarchyRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AllureAndroidJUnit4::class)
abstract class BaseTest {

    // @get:Rule val screenshotRule = ScreenshotRule(mode = ScreenshotRule.Mode.FAILURE)

    @get:Rule
    val logcatRule = LogcatRule()

    @get:Rule
    val windowHierarchyRule = WindowHierarchyRule()

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    open fun setUp() {
        activityTestRule.launchActivity(Intent())
    }
}