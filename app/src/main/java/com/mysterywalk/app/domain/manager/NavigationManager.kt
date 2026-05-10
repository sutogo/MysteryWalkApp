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
    private val _startSpot = MutableStateFlow<Spot?>(null)
    val startSpot: StateFlow<Spot?> = _startSpot.asStateFlow()

    private val _targetSpot = MutableStateFlow<Spot?>(null)
    val targetSpot: StateFlow<Spot?> = _targetSpot.asStateFlow()

    private val _isReturnMode = MutableStateFlow(false)
    val isReturnMode: StateFlow<Boolean> = _isReturnMode.asStateFlow()

    private val _navState = MutableStateFlow<NavigationState?>(null)
    val navState: StateFlow<NavigationState?> = _navState.asStateFlow()

    private val _isArrived = MutableStateFlow(false)
    val isArrived: StateFlow<Boolean> = _isArrived.asStateFlow()

    fun setTarget(spot: Spot, start: Spot? = null) {
        _targetSpot.value = spot
        if (start != null) {
            _startSpot.value = start
        }
        _isReturnMode.value = false
        _isArrived.value = false
        _navState.value = null
    }

    fun enableReturnMode() {
        val returnSpot = _startSpot.value
        if (returnSpot != null) {
            _targetSpot.value = returnSpot
            _isReturnMode.value = true
            _isArrived.value = false
            _navState.value = null
        }
    }

    fun updateNavState(state: NavigationState) {
        _navState.value = state
        if (state.distanceMeters <= 50) {
            _isArrived.value = true
        }
    }
    
    fun clear() {
        _startSpot.value = null
        _targetSpot.value = null
        _navState.value = null
        _isArrived.value = false
        _isReturnMode.value = false
    }
}
