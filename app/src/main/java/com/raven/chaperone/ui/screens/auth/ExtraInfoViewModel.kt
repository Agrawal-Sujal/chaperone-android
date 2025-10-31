package com.raven.chaperone.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raven.chaperone.data.local.appPref.AppPref
import com.raven.chaperone.domain.model.accounts.UpdateProfileRequest
import com.raven.chaperone.services.remote.AccountsServices
import com.raven.chaperone.utils.Utils.parseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingState(
    val userRole: Int = -1, // 0 = Wanderer, 1 = Walker
    val currentPage: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val goToHomePage: Boolean = false,

    // Wanderer specific
    val needsMobilityAssistance: Boolean = false,
    val walkingPace: Int = 1, // 0=Slow, 1=Moderate, 2=Brisk
    val companionGenderPreference: Int = 1, // 0=Male, 1=Female, 2=No Preference
    val companionLanguages: Set<Int> = emptySet(), // 0=Hindi, 1=English, 2=Tamil, 3=Telugu, 4=French
    val preferredCharities: Set<Int> = emptySet(), // 0=Bal Raksha, 1=Akshaya, 2=GiveIndia, 3=Smile, 4=HelpAge
    val reasonsForCompanion: Set<Int> = emptySet(), // 0=Safety, 1=Company, 2=Active, 3=Mobility, 4=Charity

    // Walker specific
    val photoUploaded: Boolean = false,
    val photoUrl: String = "",
    val walkerGender: Int = 1, // 0=Male, 1=Female, 2=Prefer not to say, 3=Other
    val walkerLanguages: Set<Int> = emptySet(),
    val walkerPace: Int = 1,
    val walkerBio: String = "",
    val motivations: Set<Int> = emptySet() // 0=Earn, 1=Charity, 2=Fitness, 3=Connect, 4=Flexible
)

sealed class OnboardingEvent {
    data class SelectRole(val role: Int) : OnboardingEvent()
    data object NextPage : OnboardingEvent()
    data object PreviousPage : OnboardingEvent()
    data class SetPage(val page: Int) : OnboardingEvent()

    // Wanderer events
    data class SetMobilityAssistance(val needs: Boolean) : OnboardingEvent()
    data class SetWalkingPace(val pace: Int) : OnboardingEvent()
    data class SetGenderPreference(val gender: Int) : OnboardingEvent()
    data class ToggleLanguage(val languageIndex: Int) : OnboardingEvent()
    data class ToggleCharity(val charityIndex: Int) : OnboardingEvent()
    data class ToggleReason(val reasonIndex: Int) : OnboardingEvent()

    // Walker events
    data object UploadPhoto : OnboardingEvent()
    data class SetWalkerGender(val gender: Int) : OnboardingEvent()
    data class ToggleWalkerLanguage(val languageIndex: Int) : OnboardingEvent()
    data class SetWalkerPace(val pace: Int) : OnboardingEvent()
    data class SetWalkerBio(val bio: String) : OnboardingEvent()
    data class ToggleMotivation(val motivationIndex: Int) : OnboardingEvent()

    data object SubmitApplication : OnboardingEvent()
}


@HiltViewModel
class ExtraInfoViewModel @Inject constructor(
    val accountsServices: AccountsServices,
    val appPref: AppPref,
    @param:ApplicationContext val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.SelectRole -> {
                _state.value = _state.value.copy(
                    userRole = event.role,
                    currentPage = 1
                )
            }

            is OnboardingEvent.NextPage -> {
                val maxPage = if (_state.value.userRole == 0) 2 else 2 // Both have 3 screens total
                if (_state.value.currentPage < maxPage) {
                    _state.value = _state.value.copy(
                        currentPage = _state.value.currentPage + 1
                    )
                }
            }

            is OnboardingEvent.PreviousPage -> {
                if (_state.value.currentPage > 0) {
                    _state.value = _state.value.copy(
                        currentPage = _state.value.currentPage - 1
                    )
                }
            }

            is OnboardingEvent.SetPage -> {
                _state.value = _state.value.copy(currentPage = event.page)
            }

            is OnboardingEvent.SetMobilityAssistance -> {
                _state.value = _state.value.copy(needsMobilityAssistance = event.needs)
            }

            is OnboardingEvent.SetWalkingPace -> {
                _state.value = _state.value.copy(walkingPace = event.pace)
            }

            is OnboardingEvent.SetGenderPreference -> {
                _state.value = _state.value.copy(companionGenderPreference = event.gender)
            }

            is OnboardingEvent.ToggleLanguage -> {
                val current = _state.value.companionLanguages
                _state.value = _state.value.copy(
                    companionLanguages = if (current.contains(event.languageIndex)) {
                        current - event.languageIndex
                    } else {
                        current + event.languageIndex
                    }
                )
            }

            is OnboardingEvent.ToggleCharity -> {
                val current = _state.value.preferredCharities
                _state.value = _state.value.copy(
                    preferredCharities = if (current.contains(event.charityIndex)) {
                        current - event.charityIndex
                    } else {
                        current + event.charityIndex
                    }
                )
            }

            is OnboardingEvent.ToggleReason -> {
                val current = _state.value.reasonsForCompanion
                _state.value = _state.value.copy(
                    reasonsForCompanion = if (current.contains(event.reasonIndex)) {
                        current - event.reasonIndex
                    } else {
                        current + event.reasonIndex
                    }
                )
            }

            is OnboardingEvent.UploadPhoto -> {
                _state.value = _state.value.copy(photoUploaded = true)
            }

            is OnboardingEvent.SetWalkerGender -> {
                _state.value = _state.value.copy(walkerGender = event.gender)
            }

            is OnboardingEvent.ToggleWalkerLanguage -> {
                val current = _state.value.walkerLanguages
                _state.value = _state.value.copy(
                    walkerLanguages = if (current.contains(event.languageIndex)) {
                        current - event.languageIndex
                    } else {
                        current + event.languageIndex
                    }
                )
            }

            is OnboardingEvent.SetWalkerPace -> {
                _state.value = _state.value.copy(walkerPace = event.pace)
            }

            is OnboardingEvent.SetWalkerBio -> {
                if (event.bio.length <= 250) {
                    _state.value = _state.value.copy(walkerBio = event.bio)
                }
            }

            is OnboardingEvent.ToggleMotivation -> {
                val current = _state.value.motivations
                _state.value = _state.value.copy(
                    motivations = if (current.contains(event.motivationIndex)) {
                        current - event.motivationIndex
                    } else {
                        current + event.motivationIndex
                    }
                )
            }

            is OnboardingEvent.SubmitApplication -> {
                submitApplication()
            }
        }
    }

    private fun submitApplication() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                // Simulate API call

                val data = _state.value.toUpdateProfileRequest()

                val response = parseResponse(accountsServices.updateProfile(data))

                if (response.isFailed) {
                    val errorResponse = response.error
                    val error =
                        if (errorResponse != null)
                            errorResponse.detail ?: "Unknown error"
                        else
                            "Something went wrong"
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error
                    )
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
                if (response.isSuccess) {
                    if (_state.value.userRole == 1)
                        appPref.userRole(true)
                    else appPref.userRole(false)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        goToHomePage = true
                    )
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to submit application: ${e.message}"
                )
                Toast.makeText(context, "Failed to submit application: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun OnboardingState.toUpdateProfileRequest(): UpdateProfileRequest {
    return if (userRole == 1) {
        // 🟣 Walker
        UpdateProfileRequest(
            is_walker = true,
            photo_url = photoUrl,
            about_yourself = walkerBio,
            male = walkerGender == 0,
            female = walkerGender == 1,
            need_mobility_assistance = false, // not applicable for walker
            walking_pace_ids = listOf(walkerPace),
            language_ids = walkerLanguages.toList(),
            charity_ids = emptyList() // not applicable for walker
        )
    } else {
        // 🟢 Wanderer
        UpdateProfileRequest(
            is_walker = false,
            photo_url = "",
            about_yourself = "",
            male = companionGenderPreference == 0 || companionGenderPreference == 2,
            female = companionGenderPreference == 1 || companionGenderPreference == 2,
            need_mobility_assistance = needsMobilityAssistance,
            walking_pace_ids = listOf(walkingPace),
            language_ids = companionLanguages.toList(),
            charity_ids = preferredCharities.toList()
        )
    }
}
