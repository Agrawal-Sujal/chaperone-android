package com.raven.chaperone.utils

import android.util.Log
import com.google.gson.Gson
import com.raven.chaperone.domain.model.ApiError
import com.raven.chaperone.domain.model.ResponseError
import retrofit2.Response

object Utils {

    fun <T> parseResponse(response: Response<T>): Resource<T> {
        if (response.isSuccessful) {
            Log.d("Response Body ", response.body()!!.toString())
            return Resource.success(response.body()!!)
        }
        val error = response.error()
        if (error == null) {
            return Resource.failure(
                error = ApiError(
                    detail = ResponseError.getError(
                        response
                    ).genericToast
                )
            )
        }
        try {
            val gson = Gson()
            val errorResponse = gson.fromJson(error, ApiError::class.java)
            if (errorResponse == null || errorResponse.detail == null) {
                return Resource.failure(
                    error = ApiError(
                        detail = ResponseError.getError(
                            response
                        ).genericToast
                    )
                )
            }

            return Resource.failure(error = errorResponse)
        } catch (e: Exception) {
            return Resource.failure(
                error = ApiError(
                    detail = ResponseError.getError(
                        response
                    ).genericToast
                )
            )
        }
    }

    /** Get human readable error.
     *
     * **CAUTION:** If this function is called once, calling it further with the same [Response] instance will result in an empty
     * string. Store this function's result for multiple use cases.*/
    fun <T> Response<T>.error(): String? = this.errorBody()?.string()
}


fun convertToISODate(inputDate: String): String {
    val inputFormatter = java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy")
    val outputFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = java.time.LocalDate.parse(inputDate, inputFormatter)
    return date.format(outputFormatter)
}
