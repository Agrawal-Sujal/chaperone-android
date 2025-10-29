package com.raven.chaperone.domain.model.accounts

data class UpdateProfileRequest(
    val is_walker: Boolean,
    val photo_url: String = "",
    val about_yourself: String = "",
    val male: Boolean,
    val female: Boolean = false,
    val need_mobility_assistance: Boolean = false,
    val walking_pace_ids: List<Int>,
    val language_ids: List<Int>,
    val charity_ids: List<Int> = emptyList()
)
