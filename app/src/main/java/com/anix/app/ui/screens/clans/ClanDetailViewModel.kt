package com.anix.app.ui.screens.clans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Clan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ClanDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val clan: Clan? = null
)

class ClanDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.getClanRepository()

    private val _uiState = MutableStateFlow(ClanDetailUiState())
    val uiState: StateFlow<ClanDetailUiState> = _uiState.asStateFlow()

    fun loadClan(clanId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getClanDetail(clanId).onSuccess { clan ->
                _uiState.value = _uiState.value.copy(clan = clan, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
