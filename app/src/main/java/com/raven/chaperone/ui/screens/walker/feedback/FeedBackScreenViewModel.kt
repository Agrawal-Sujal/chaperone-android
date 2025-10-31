package com.raven.chaperone.ui.screens.walker.feedback

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.services.remote.FeedbackServices
import com.raven.chaperone.services.remote.WalkerFeedback
import com.raven.chaperone.services.remote.WandererFeedback
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class FeedBackScreenViewModel @Inject constructor(val feedbackServices: FeedbackServices) :
    ViewModel() {

    val walkerRating = mutableStateOf(0)
    val feedback = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val isSuccess = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun onRatingSelected(rating: Int) {
        walkerRating.value = rating
        errorMessage.value = null
    }

    fun onFeedbackChange(newFeedback: String) {
        feedback.value = newFeedback
    }

    fun sendFeedback(wandererId: Int) {
        if (walkerRating.value == 0) {
            errorMessage.value = "Please rate your walker before submitting."
            return
        }

        viewModelScope.launch {
            errorMessage.value = null
            isLoading.value = true
            try {
                val response = parseResponse(
                    feedbackServices.AddWandererFeedback(
                        WandererFeedback(
                            wanderer_id = wandererId,
                            rating = walkerRating.value,
                        )
                    )
                )
                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    isLoading.value = false
                    isSuccess.value = false
                    errorMessage.value =
                        error ?: "Failed to load walks"
                }
                if (response.isSuccess) {
                    val data = response.data
                    if (data != null) {
                        isLoading.value = false
                        isSuccess.value = true
                    } else {
                        isLoading.value = false
                        isSuccess.value = false
                        errorMessage.value = "Something went wrong"
                    }
                }

            } catch (e: Exception) {
                isLoading.value = false
                isSuccess.value = false
                errorMessage.value =
                    e.message ?: "Failed to load data. Please try again."

            }
        }
    }
}