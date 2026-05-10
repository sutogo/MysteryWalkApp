package com.mysterywalk.app.ui.reward

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysterywalk.app.data.remote.WikimediaApi
import com.mysterywalk.app.domain.usecase.ProcessArrivalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RewardUiState(
    val isLoading: Boolean = true,
    val distanceWalkedMeters: Int = 0,
    val earnedXp: Int = 0,
    val totalXp: Int = 0,
    val newLevel: Int = 1,
    val isLevelUp: Boolean = false,
    val imageUrl: String? = null,
    val destinationName: String? = null,
    val destinationCategory: String? = null
)

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val processArrivalUseCase: ProcessArrivalUseCase,
    private val wikimediaApi: WikimediaApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardUiState())
    val uiState: StateFlow<RewardUiState> = _uiState.asStateFlow()

    fun loadReward(distanceMeters: Int, lat: Double, lon: Double, name: String?, category: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Process XP and Level
            val arrivalResult = processArrivalUseCase(distanceMeters)

            // 2. Fetch image from Wikimedia API
            var imageUrl: String? = null
            try {
                val coords = "$lat|$lon"
                val response = wikimediaApi.searchImageByLocation(coords = coords)
                
                val pages = response.query?.pages
                if (pages != null && pages.isNotEmpty()) {
                    val firstPage = pages.values.firstOrNull()
                    val imageInfo = firstPage?.imageinfo?.firstOrNull()
                    imageUrl = imageInfo?.url
                }
            } catch (e: Exception) {
                // Ignore API error, will fallback to icon
                e.printStackTrace()
            }

            // 3. Update State
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                distanceWalkedMeters = distanceMeters,
                earnedXp = arrivalResult.earnedXp,
                totalXp = arrivalResult.newTotalXp,
                newLevel = arrivalResult.newLevel,
                isLevelUp = arrivalResult.isLevelUp,
                imageUrl = imageUrl,
                destinationName = name ?: "Unknown Spot",
                destinationCategory = category
            )
        }
    }
}
