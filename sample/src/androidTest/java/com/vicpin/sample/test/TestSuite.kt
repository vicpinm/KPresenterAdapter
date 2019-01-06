package com.vicpin.sample.test

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(AutoBindingActivityTests::class, ManualBindingActivityTests::class)
class TestSuite