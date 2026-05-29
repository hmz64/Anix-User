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
    val clans: List<Clan> = emptyList(),
    val leaderboard: List<Clan> = emptyList(),
    val myClan: Clan? = null,
    val selectedTab: Int = 0,
    val isCreatingClan: Boolean = false,
    val createSuccess: Boolean = false,
    val createError: String? = null
)

class ClanListViewModel : ViewModel() {
    private val repo = ServiceLocator.getClanRepository()

    private val _uiState = MutableStateFlow(ClanListUiState())
    val uiState: StateFlow<ClanListUiState> = _uiState.asStateFlow()

    init {
        loadClans()
        loadLeaderboard()
        loadMyClan()
    }

    fun loadClans() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getClans()
                .onSuccess { clans ->
                    _uiState.value = _uiState.value.copy(clans = clans, isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
        }
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            repo.getClanLeaderboard()
                .onSuccess { list ->
                    _uiState.value = _uiState.value.copy(leaderboard = list)
                }
        }
    }

    fun loadMyClan() {
        viewModelScope.launch {
            repo.getMyClan()
                .onSuccess { clan ->
                    _uiState.value = _uiState.value.copy(myClan = clan)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(myClan = null)
                }
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun createClan(name: String, tag: String, description: String) {
        _uiState.value = _uiState.value.copy(isCreatingClan = true, createError = null, createSuccess = false)
        viewModelScope.launch {
            repo.createClan(name, tag, description)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isCreatingClan = false, createSuccess = true)
                    loadMyClan()
                    loadClans()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isCreatingClan = false, createError = e.message)
                }
        }
    }
}