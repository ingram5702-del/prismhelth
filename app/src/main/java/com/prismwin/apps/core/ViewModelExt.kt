package com.prismwin.apps.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prismwin.apps.PrismApplication

@Composable
inline fun <reified T : ViewModel> appViewModel(): T {
    val app = LocalContext.current.applicationContext as PrismApplication
    val factory = remember(app) { AppViewModelFactory(app, app.appContainer) }
    return viewModel(factory = factory)
}
