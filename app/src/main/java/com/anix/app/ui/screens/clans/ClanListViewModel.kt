package com.anix.app.ui.screens.clans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Clan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClanListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val clans: List<Clan> = emptyList()
)

class ClanListViewModel : ViewModel() {
    private val repo = ServiceLocator.getClanRepository()

    private val _uiState = MutableStateFlow(ClanListUiState())
    val uiState: StateFlow<ClanListUiState> = _uiState.asStateFlow()

    init {
        loadClans()
    }

    fun loadClans() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getClans().onSuccess { clans ->
                _uiState.value = _uiState.value.copy(clans = clans, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
