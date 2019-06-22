package com.theportal.network

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.theportal.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Created by codexalters on 9/2/18.
 */
object WebServiceResponseHandler {

    interface DataHandlerWithHeaders {
        fun onSuccess(data: JsonElement, message: String)
        fun onFailure(message: String)
        fun noInternetConnection()
        fun sessionExpired()
    }

    interface DataHandler {
        fun onSuccess(data: JsonElement, message: String)
        fun onFailure(message: String)
        fun noInternetConnection()
        fun sessionExpired()

    }

    interface DataHandlerWithLimit {
        fun onSuccess(data: JsonElement, limit: Limit, message: String)
        fun onFailure(message: String)
        fun noInternetConnection()
        fun sessionExpired()

    }

    fun handleApiResponse(
        apiCall: retrofit2.Call<ResponseBody>,
        dataHandle: DataHandler
    ): Call<ResponseBody> {

        apiCall.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (call!!.isCanceled) {
                    Log.e("WebServiceResponseHndlr", "onFailure : call.isCanceled : " + call.isCanceled)
                } else if (t is IOException && t.message.equals(Constants.NO_INTERNET, true)) {
                    dataHandle.noInternetConnection()
                } else if (t is SocketTimeoutException) {
                    dataHandle.onFailure("Time out") //
                } else {
                    dataHandle.onFailure("Internal Server Error!") //
                }

                t!!.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                if (response!!.body() != null) {
                    if (response.body()!!.status == 200) { //success

                        dataHandle.onSuccess(
                            response.body()!!.data!!,
                            response.body()!!.message
                        )

                    } else if (response.body()!!.status == 401) {// wrongToken //No Token Send
                        dataHandle.sessionExpired()
                    } else if (response.body()!!.status == 400) { // wrong data send
                        dataHandle.onFailure(response.body()!!.message)
                    } else if (response.body()!!.status == 404) { // result not found
                        dataHandle.onFailure(response.body()!!.message)
                    } else if (response.body()!!.status == 405) { //Invalid Request Method
                        dataHandle.onFailure(response.body()!!.message)
                    } else {
                        dataHandle.onFailure(response.body()!!.message)
                    }
                } else {
                    dataHandle.onFailure("Internal Server Error!") // retrofit message
                }
            }
        })
        return apiCall
    }

    fun handleApiResponseWithLimit(
        apiCall: retrofit2.Call<ResponseBody>,
        dataHandle: DataHandlerWithLimit
    ): Call<ResponseBody> {

        apiCall.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (call!!.isCanceled) {
                    Log.e("WebServiceResponseHndlr", "onFailure : call.isCanceled : " + call.isCanceled)
                } else if (t is IOException && t.message.equals(Constants.NO_INTERNET, true)) {
                    dataHandle.noInternetConnection()
                } else if (t is SocketTimeoutException) {
                    dataHandle.onFailure("Time out") //
                } else {
                    dataHandle.onFailure("Internal Server Error!") //
                }

                t!!.printStackTrace()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                if (response!!.body() != null) {
                    if (response.body()!!.status == 200) { //success

                        dataHandle.onSuccess(
                            response.body()!!.data!!,
                            response.body()!!.limit,
                            response.body()!!.message
                        )

                    } else if (response.body()!!.status == 401) {// wrongToken //No Token Send
                        dataHandle.sessionExpired()
                    } else if (response.body()!!.status == 400) { // wrong data send
                        dataHandle.onFailure(response.body()!!.message)
                    } else if (response.body()!!.status == 404) { // result not found
                        dataHandle.onFailure(response.body()!!.message)
                    } else if (response.body()!!.status == 405) { //Invalid Request Method
                        dataHandle.onFailure(response.body()!!.message)
                    } else {
                        dataHandle.onFailure(response.body()!!.message)
                    }
                } else {
                    dataHandle.onFailure("Internal Server Error!") // retrofit message
                }
            }
        })
        return apiCall
    }

    fun handleApiResponseWithJsonElement(
        apiCall: Call<JsonElement>,
        dataHandler: DataHandlerWithHeaders
    ): Call<JsonElement> {

        apiCall.enqueue(object : Callback<JsonElement> {

            override fun onFailure(call: Call<JsonElement>?, t: Throwable?) {

                if (call!!.isCanceled) {
                    Log.e("WebServiceResponseHndlr", "onFailure : call.isCanceled : " + call.isCanceled)
                } else if (t is IOException && t.message.equals(Constants.NO_INTERNET, true)) {
                    dataHandler.noInternetConnection()
                } else if (t is SocketTimeoutException) {
                    dataHandler.onFailure("Time out") //
                } else {
                    dataHandler.onFailure("Internal Server Error!") //
                }

                t!!.printStackTrace()
            }

            override fun onResponse(call: Call<JsonElement>?, response: Response<JsonElement>?) {

                if (response!!.body() != null)
                    dataHandler.onSuccess(response.body()!!, "")
                else if (response.errorBody() != null) {
                    val string = response.errorBody()!!.string()
                    try {
                        dataHandler.onFailure(string)
                    } catch (e: Exception) {
                        Log.e("WebServiceResponse", "onResponse : ${e.printStackTrace()} ")
                        dataHandler.onFailure("Something went wrong!")
                    }

                } else {
                    dataHandler.onFailure("Something went wrong!")
                }


            }
        })

        return apiCall

    }

    class ResponseBody {
        @SerializedName("status")
        val status: Int? = null
        @SerializedName("message")
        val message: String = ""
        @SerializedName("data")
        val data: JsonElement? = null
        @SerializedName("limit")
        val limit: Limit = Limit()
    }

    class Limit {
        @SerializedName("TotalRecord")
        val TotalRecord: String = ""
        @SerializedName("limit")
        val limit: String = ""
        @SerializedName("continue")
        val isContinue: String = ""
    }

}