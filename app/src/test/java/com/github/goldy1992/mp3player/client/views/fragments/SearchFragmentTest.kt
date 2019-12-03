package com.github.goldy1992.mp3player.client.views.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario.FragmentAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class SearchFragmentTest : FragmentTestBase<SearchFragment>() {
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        super.setup(SearchFragment::class.java, false)
        super.addFragmentToActivity()
    }

    @Test
    fun testOnClickOnLayout() {
        val action = FragmentAction { fragment: SearchFragment? -> clickOnLayout(fragment) }
        performAction(action)
    }

    private fun clickOnLayout(fragment: SearchFragment?) {
        fragment!!.onClickOnLayout(Mockito.mock(View::class.java))
    }
}