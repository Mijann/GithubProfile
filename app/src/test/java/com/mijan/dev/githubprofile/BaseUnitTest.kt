package com.mijan.dev.githubprofile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.time.ExperimentalTime


/*
* Reference https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md
* Approach for kotlin coroutines unit testing
* */
@ExperimentalCoroutinesApi
@ExperimentalTime
open class BaseUnitTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val testRule = object : TestWatcher() {
        override fun starting(description: Description?) {
            super.starting(description)
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description?) {
            super.finished(description)
            Dispatchers.resetMain()
        }
    }
}