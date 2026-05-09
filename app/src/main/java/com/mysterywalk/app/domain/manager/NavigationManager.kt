package com.mysterywalk.app.domain.manager

import com.mysterywalk.app.domain.model.Spot
import com.mysterywalk.app.domain.usecase.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ナビゲーションの進行状況（現在の目的地、計算された残り距離や角度、到着判定）を
 * サービスとUI（ViewModel）間で共有するためのシングルトン状態管理クラス。
 */
@Singleton
class NavigationManager @Inject constructor() {
    private val _targetSpot = MutableStateFlow<Spot?>(null)
    val targetSpot: StateFlow<Spot?> = _targetSpot.asStateFlow()

    private val _navState = MutableStateFlow<NavigationState?>(null)
    val navState: StateFlow<NavigationState?> = _navState.asStateFlow()

    private val _isArrived = MutableStateFlow(false)
    val isArrived: StateFlow<Boolean> = _isArrived.asStateFlow()

    fun setTarget(spot: Spot) {
        _targetSpot.value = spot
        _isArrived.value = false
        _navState.value = null
    }

    fun updateNavState(state: NavigationState) {
        _navState.value = state
        if (state.distanceMeters <= 50) {
            _isArrived.value = true
        }
    }
    
    fun clear() {
        _targetSpot.value = null
        _navState.value = null
        _isArrived.value = false
    }
}
