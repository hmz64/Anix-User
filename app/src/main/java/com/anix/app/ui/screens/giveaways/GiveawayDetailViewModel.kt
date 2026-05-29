package com.anix.app.ui.screens.giveaways

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Giveaway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GiveawayDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val giveaway: Giveaway? = null
)

class GiveawayDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.getGiveawayRepository()

    private val _uiState = MutableStateFlow(GiveawayDetailUiState())
    val uiState: StateFlow<GiveawayDetailUiState> = _uiState.asStateFlow()

    fun loadGiveaway(giveawayId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getGiveawayDetail(giveawayId).onSuccess { giveaway ->
                _uiState.value = _uiState.value.copy(giveaway = giveaway, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun claimGiveaway(giveawayId: String) {
        viewModelScope.launch {
            repo.claimGiveaway(giveawayId)
        }
    }
}
