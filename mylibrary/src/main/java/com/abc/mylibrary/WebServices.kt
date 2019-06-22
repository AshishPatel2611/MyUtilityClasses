package com.theportal.network

import retrofit2.Call
import retrofit2.http.*

/**
 * Created by Ashish on 7/3/19.
 */
interface WebServices {

    companion object {

        const val LOGIN = "login"
        const val LOG_OUT = "logout"
        const val EDIT_PROFILE = "editProfile"
        const val GET_TERMS = "getTerms"
        const val SOCIAL_LOGIN = "sociallogin"
        const val GET_PORTAL_LIST = "getPortallist"
        const val ADD_RIDE = "addRide"
        const val END_RIDE = "endRide"
        const val GET_RENTAL_HISTORY = "getRideHistoryList"
        const val CHECK_MOBILE_NUMBER = "checkMobileNo"
        const val CHECK_QR_CODE = "checkQrCode"
        const val CHECK_ONGOING_RIDE = "checkOnGoingRide"
        const val CHECK_APK_VERSION = "checkAppVersionNew"
        const val RESEND_OTP = "resendOTP"


        /*------------- will be removed due to BrainTree Payment Gateway issue ---------------*/

        const val GET_PAYMENT_METHODS = "getCustomerCardList"
        const val AUTO_RELOAD = "autoReload"
        const val GET_WALLET_BALANCE = "getWalletBalance"
        const val DEBIT_AMOUNT_FROM_WALLET = "DebitAmountfromWallet"
        const val START_DEBIT_CARD_TRANSACTIN = "createTransaction"
        const val SET_DEFAULT_PAYMENT_METHOD = "setDefaultCard"
        const val DELETE_PAYMENT_METHOD = "removeCustomerCard"
        const val CREATE_PAYMENT_METHOD = "createCreditCard"

    }

    @FormUrlEncoded
    @POST(CHECK_APK_VERSION)
    fun getLatestApkVersion(@Field("nAppType") nAppType: String): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(LOGIN)
    fun login(
        @Field("vCountryCode") vCountryCode: String,
        @Field("vMobileNo") vMobileNo: String,
        @Field("vPushToken") vPushToken: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @GET(LOG_OUT)
    fun logout(): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(EDIT_PROFILE)
    fun editProfile(@FieldMap fields: HashMap<String, String>): Call<WebServiceResponseHandler.ResponseBody>

    @GET(GET_TERMS)
    fun getTerms(): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(SOCIAL_LOGIN)
    fun performSocialLogin(
        @Field("vFirstName") vFirstName: String,
        @Field("vLastName") vLastName: String,
        @Field("vProviderKey") vProviderKey: String,
        @Field("vMobileNo") vMobileNo: String,
        @Field("vEmail") vEmail: String,
        @Field("vPushToken") vPushToken: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(GET_PORTAL_LIST)
    fun getPortalList(
        @Field("dLat") dLat: Double,
        @Field("dLng") dLng: Double
    ): Call<WebServiceResponseHandler.ResponseBody>


    @FormUrlEncoded
    @POST(ADD_RIDE)
    fun addRide(
        @Field("tStartTime") tStartTime: String,
        @Field("vPortalCode") vPortalCode: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(END_RIDE)
    fun endRide(
        @Field("nRideId") nRideId: String,
        @Field("vPortalCode") portalCode: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(CHECK_QR_CODE)
    fun checkQRcode(
        @Field("vPortalCode") vPortalCode: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @GET(CHECK_ONGOING_RIDE)
    fun checkOnGoingRide(): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(GET_RENTAL_HISTORY)
    fun getRentalHistory(@Field("offset") offset: Int): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(CHECK_MOBILE_NUMBER)
    fun sendOTP(
        @Field("vCountryCode") dialCode: String,
        @Field("vMobileNo") mobileNo: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(RESEND_OTP)
    fun resendOTP(
        @Field("vCountryCode") dialCode: String,
        @Field("vMobileNo") mobileNo: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(START_DEBIT_CARD_TRANSACTIN)
    fun startTransactionOnDefaultPaymentMethod(
        @Field("Amount") Amount: String,
        @Field("customerId") customerId: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(START_DEBIT_CARD_TRANSACTIN)
    fun startTransaction(
        @Field("Amount") Amount: String,
        @Field("customerId") customerId: String,
        @Field("Cardtoken") Cardtoken: String,
        @Field("nonce") nonce: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(DEBIT_AMOUNT_FROM_WALLET)
    fun debitRentalChargeFromWallet(@Field("dAmount") amount: String): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(AUTO_RELOAD)
    fun autoCreditToWallet(
        @Field("isAutoReload") isAutoReload: Int,
        @Field("Amount") amount: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @GET(GET_WALLET_BALANCE)
    fun getWalletBalance(): Call<WebServiceResponseHandler.ResponseBody>


    @FormUrlEncoded
    @POST(GET_PAYMENT_METHODS)
    fun getCustomerCardList(@Field("customerId") brainTreeCustomerId: String): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(CREATE_PAYMENT_METHOD)
    fun crateCreditCard(
        @Field("customerId") brainTreeCustomerId: String,
        @Field("cardNumber") cardNumber: String,
        @Field("expirationDate") cardExpiryDate: String,
        @Field("cvv") cardCVV: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(SET_DEFAULT_PAYMENT_METHOD)
    fun setDefaultPaymentMethod(
        @Field("customerId") brainTreeCustomerId: String,
        @Field("Cardtoken") Cardtoken: String
    ): Call<WebServiceResponseHandler.ResponseBody>

    @FormUrlEncoded
    @POST(DELETE_PAYMENT_METHOD)
    fun deletePaymentMethod(@Field("Cardtoken") brainTreeCustomerId: String): Call<WebServiceResponseHandler.ResponseBody>

}