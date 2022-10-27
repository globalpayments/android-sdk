package com.globalpatments.android.sdk.merchant3ds.networking

import com.globalpatments.android.sdk.merchant3ds.networking.models.request.*
import com.globalpatments.android.sdk.merchant3ds.networking.models.response.*
import retrofit2.http.Body
import retrofit2.http.POST

interface Merchant3DSApi {
    @POST("/8888/accessToken")
    suspend fun accessToken(@Body accessTokenRequest: AccessTokenRequest): AccessTokenResponse

    @POST("/8888/checkEnrollment")
    suspend fun checkEnrollment(@Body checkEnrollmentRequest: CheckEnrollmentRequest): CheckEnrollmentResponse

    @POST("/8888/initiateAuthentication")
    suspend fun initiateAuthentication(@Body initiateAuthenticationParams: InitiateAuthenticationParams): InitiateAuthenticationResponse

    @POST("/8888/getAuthenticationData")
    suspend fun getAuthenticationData(@Body getAuthenticationDataRequest: GetAuthenticationDataRequest): GetAuthenticationDataResponse

    @POST("/8888/authorizationData")
    suspend fun authorizationData(@Body authorizationDataRequest: AuthorizationDataRequest): AuthorizationDataResponse
}