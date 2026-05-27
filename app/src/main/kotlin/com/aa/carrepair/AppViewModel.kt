package com.aa.carrepair

import androidx.lifecycle.ViewModel
import com.aa.carrepair.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * Root ViewModel that keeps the app launch focused on the Garage Copilot chat surface.
 */
@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {

    val startDestination: StateFlow<String?> = MutableStateFlow(Screen.Root.route)
}
