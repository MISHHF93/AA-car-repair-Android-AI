package com.aa.carrepair.feature.dtc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.usecase.dtc.AnalyzeDtcUseCase
import com.aa.carrepair.domain.usecase.dtc.SearchDtcUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DtcViewModel @Inject constructor(
    private val analyzeDtcUseCase: AnalyzeDtcUseCase,
    private val searchDtcUseCase: SearchDtcUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DtcUiState())
    val uiState: StateFlow<DtcUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .flatMapLatest { query -> searchDtcUseCase(query) }
            .onEach { results -> _uiState.update { it.copy(searchResults = results) } }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQuery.value = query
    }

    fun analyzeCode(code: String) {
        if (code.isBlank()) return
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = analyzeDtcUseCase(code)) {
                is DataResult.Success -> {
                    _uiState.update { it.copy(selectedCode = result.data, isLoading = false) }
                }
                is DataResult.Error -> {
                    Timber.e(result.exception, "DTC analysis failed")
                    _uiState.update { it.copy(isLoading = false, error = "Could not analyze code: $code") }
                }
                is DataResult.Loading -> Unit
            }
        }
    }

    fun initWithCode(code: String) {
        if (code.isNotBlank()) {
            _uiState.update { it.copy(searchQuery = code) }
            analyzeCode(code)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
