package com.theportal.network


import androidx.annotation.Nullable
import com.google.gson.JsonElement
import java.io.Serializable


/**
 * Created by Ashish on 4/12/18.
 */

class ApiResponse : Serializable {

    var status: ApiResponseStatus? = null

    @Nullable
    var data: JsonElement? = null

    @Nullable
    var message: String = ""

    @Nullable
    var limit: WebServiceResponseHandler.Limit = WebServiceResponseHandler.Limit()


    private fun ApiResponse(
        status: ApiResponseStatus,
        data: JsonElement?,
        error: String
    ): ApiResponse {
        this.status = status
        this.data = data
        this.message = error
        return this
    }

    private fun ApiResponseWithLimit(
        status: ApiResponseStatus,
        data: JsonElement?,
        limit: WebServiceResponseHandler.Limit,
        error: String
    ): ApiResponse {
        this.status = status
        this.data = data
        this.message = error
        this.limit = limit
        return this
    }

    fun loading(): ApiResponse {
        return ApiResponse(ApiResponseStatus.LOADING, null, "")
    }

    fun success(data: JsonElement, msg: String): ApiResponse {
        return ApiResponse(ApiResponseStatus.SUCCESS, data, msg)
    }

    fun success(data: JsonElement, limit: WebServiceResponseHandler.Limit, msg: String): ApiResponse {
        return ApiResponseWithLimit(ApiResponseStatus.SUCCESS, data, limit, msg)
    }

    fun error(error: String): ApiResponse {
        return ApiResponse(ApiResponseStatus.ERROR, null, error)
    }

    fun sessionExpired(): ApiResponse {
        return ApiResponse(ApiResponseStatus.SESSION_EXPIRED, null, "Session Expired! Log in again.")
    }

    override fun toString(): String {
        return "ApiResponse(status=$status, message='$message'"
    }


}