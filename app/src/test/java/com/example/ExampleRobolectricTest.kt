package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.viewmodel.WealthFlowViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WealthFlow", appName)
  }

  @Test
  fun `viewModel initialization should have valid default results`() {
    val vm = WealthFlowViewModel()
    val state = vm.uiState.value
    assertNotNull(state.sipResult)
    assertEquals(15, state.sipResult.yearlyBreakdown.size)
    assertNotNull(state.swpResult)
    assertEquals(20, state.swpResult.yearlyBreakdown.size)
  }

  @Test
  fun `soundManager profile selection should switch correctly`() {
    val vm = WealthFlowViewModel()
    vm.setSoundProfile(com.example.utils.SoundManager.SoundProfile.HAPTIC_CLICK)
    assertEquals(com.example.utils.SoundManager.SoundProfile.HAPTIC_CLICK, vm.uiState.value.soundProfile)
    vm.toggleSound()
    assertEquals(false, vm.uiState.value.isSoundEnabled)
  }
}
