package com.raven.chaperone.utils

import com.raven.chaperone.domain.model.ApiError

data class Resource<T>(val status: Status, val data: T?, val error: ApiError? = null) {

    inline val isSuccess get() = status.isSuccessful()
    inline val isFailed get() = status.isFailed()
    inline val isLoading get() = status.isLoading()

    enum class Status {
        LOADING, FAILED, SUCCESS;

        fun isSuccessful() = this == SUCCESS

        fun isFailed() = this == FAILED

        fun isLoading() = this == LOADING
    }

    companion object {
        fun <S> success(data: S): Resource<S> =
            Resource(Status.SUCCESS, data)


        fun <S> failure(error: ApiError? = null, data: S? = null): Resource<S> =
            Resource(Status.FAILED, data, error)

        fun <S> loading(data: S? = null): Resource<S> =
            Resource(Status.LOADING, data)
    }
}