package com.mysterywalk.app.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysterywalk.app.data.location.LocationClient
import com.mysterywalk.app.domain.manager.NavigationManager
import com.mysterywalk.app.domain.model.Spot
import com.mysterywalk.app.domain.repository.SpotRepository
import com.mysterywalk.app.service.LocationService
import com.mysterywalk.app.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotRepository: SpotRepository,
    private val locationClient: LocationClient,
    val navigationManager: NavigationManager,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    private val _uiState = MutableStateFlow<NavUiState>(
        if (navigationManager.targetSpot.value != null) NavUiState.Navigating else NavUiState.Idle
    )
    val uiState: StateFlow<NavUiState> = _uiState.asStateFlow()

    fun findSpotAndStartNavigation(radiusMeters: Int) {
        viewModelScope.launch {
            _uiState.value = NavUiState.Loading

            try {
                // 初期位置を取得するために一瞬だけFlowを回して最初の要素を取得
                val currentLocation = locationClient.getLocationUpdates(1000L).first()
                val spot = spotRepository.getRandomSpot(
                    radiusMeters = radiusMeters,
                    currentLat = currentLocation.latitude,
                    currentLon = currentLocation.longitude
                )

                val startSpot = Spot(
                    id = -1L,
                    lat = currentLocation.latitude,
                    lon = currentLocation.longitude,
                    name = "出発地点",
                    category = "Home"
                )

                // ナビゲーションマネージャーに目的地をセットし、サービスを起動
                navigationManager.setTarget(spot, startSpot)
                startLocationService()
                
                _uiState.value = NavUiState.Navigating

            } catch (e: Exception) {
                _uiState.value = NavUiState.Error(e.message ?: "エラーが発生しました")
            }
        }
    }

    fun stopNavigation() {
        stopLocationService()
        navigationManager.clear()
        _uiState.value = NavUiState.Idle
    }

    fun enableReturnMode() {
        navigationManager.enableReturnMode()
        _uiState.value = NavUiState.Navigating
    }

    private fun startLocationService() {
        Intent(context, LocationService::class.java).also { intent ->
            intent.action = LocationService.ACTION_START
            context.startForegroundService(intent)
        }
    }

    private fun stopLocationService() {
        Intent(context, LocationService::class.java).also { intent ->
            intent.action = LocationService.ACTION_STOP
            context.startService(intent)
        }
    }
}

sealed class NavUiState {
    object Idle : NavUiState()
    object Loading : NavUiState()
    object Navigating : NavUiState()
    data class Error(val message: String) : NavUiState()
}
