package com.prismwin.apps.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.prismwin.apps.AppContainer
import com.prismwin.apps.PrismApplication
import com.prismwin.apps.ui.viewmodel.HomeViewModel
import com.prismwin.apps.ui.viewmodel.OnboardingViewModel
import com.prismwin.apps.ui.viewmodel.ReactionViewModel
import com.prismwin.apps.ui.viewmodel.SequenceViewModel
import com.prismwin.apps.ui.viewmodel.SettingsViewModel
import com.prismwin.apps.ui.viewmodel.StatsViewModel
import com.prismwin.apps.ui.viewmodel.TargetViewModel
import com.prismwin.apps.ui.viewmodel.WebGateViewModel

class AppViewModelFactory(
    private val application: PrismApplication,
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(container.gameRepository, container.settingsRepository) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(container.settingsRepository) as T
            }
            modelClass.isAssignableFrom(ReactionViewModel::class.java) -> {
                ReactionViewModel(container.gameRepository, container.settingsRepository) as T
            }
            modelClass.isAssignableFrom(TargetViewModel::class.java) -> {
                TargetViewModel(container.gameRepository, container.settingsRepository) as T
            }
            modelClass.isAssignableFrom(SequenceViewModel::class.java) -> {
                SequenceViewModel(container.gameRepository, container.settingsRepository) as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                StatsViewModel(container.gameRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    settingsRepository = container.settingsRepository,
                    reminderScheduler = container.reminderScheduler
                ) as T
            }
            modelClass.isAssignableFrom(WebGateViewModel::class.java) -> {
                WebGateViewModel(
                    webConfigRepository = container.webConfigRepository,
                    context = application
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}
