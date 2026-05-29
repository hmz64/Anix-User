package com.anix.app.ui.screens.giveaways

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Giveaway
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GiveawayListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val giveaways: List<Giveaway> = emptyList()
)

class GiveawayListViewModel : ViewModel() {
    private val repo = ServiceLocator.getGiveawayRepository()

    private val _uiState = MutableStateFlow(GiveawayListUiState())
    val uiState: StateFlow<GiveawayListUiState> = _uiState.asStateFlow()

    init {
        loadGiveaways()
    }

    fun loadGiveaways() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getGiveaways().onSuccess { giveaways ->
                _uiState.value = _uiState.value.copy(giveaways = giveaways, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
