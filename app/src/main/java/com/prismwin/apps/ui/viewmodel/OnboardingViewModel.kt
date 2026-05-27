package com.prismwin.apps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prismwin.apps.R
import com.prismwin.apps.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val body: String,
    val imageRes: Int
)

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val currentPageIndex: Int = 0,
    val pages: List<OnboardingPage> = listOf(
        OnboardingPage(
            title = "Welcome",
            body = "Prisma Win trains reaction speed, accuracy, and working memory in short sessions.",
            imageRes = R.drawable.onboarding_reaction
        ),
        OnboardingPage(
            title = "How to train",
            body = "Aim for 5-10 minutes a day and track your progress in stats.",
            imageRes = R.drawable.onboarding_training
        ),
        OnboardingPage(
            title = "Daily challenges",
            body = "New goals are generated every day. Complete them to accelerate progress.",
            imageRes = R.drawable.onboarding_challenges
        )
    )
)

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collectLatest { settings ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isCompleted = settings.onboardingCompleted
                    )
                }
            }
        }
    }

    fun nextPage() {
        val state = _uiState.value
        if (state.currentPageIndex < state.pages.lastIndex) {
            _uiState.update { it.copy(currentPageIndex = it.currentPageIndex + 1) }
        }
    }

    fun previousPage() {
        val state = _uiState.value
        if (state.currentPageIndex > 0) {
            _uiState.update { it.copy(currentPageIndex = it.currentPageIndex - 1) }
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
        }
    }
}
