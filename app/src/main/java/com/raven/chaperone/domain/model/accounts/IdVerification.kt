package com.raven.chaperone.domain.model.accounts

data class IdVerificationRequest(
    val name: String,
    val date_of_birth: String,
    val phone_number : String
)

