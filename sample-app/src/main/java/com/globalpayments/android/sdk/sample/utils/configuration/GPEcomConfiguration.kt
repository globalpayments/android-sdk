package com.globalpayments.android.sdk.sample.utils.configuration

import com.global.api.entities.enums.Channel
import com.global.api.entities.enums.Secure3dVersion
import com.global.api.entities.enums.ShaHashType
import com.globalpayments.android.sdk.sample.BuildConfig

data class GPEcomConfiguration(
    val accountId: String? = null,
    val merchantId: String? = null,
    val rebatePassword: String? = null,
    val refundPassword: String? = null,
    val sharedSecret: String? = null,
    var channel: Channel? = null,
    val challengeNotificationUrl: String? = null,
    val merchantContactUrl: String? = null,
    val methodNotificationUrl: String? = null,
    var secure3dVersion: Secure3dVersion? = null,
    var shaHashType: ShaHashType? = null,
    val apiKey: String? = null
) {
    companion object {
        @JvmStatic
        fun fromBuildConfig(): GPEcomConfiguration {
            return GPEcomConfiguration(
                BuildConfig.ACCOUNT_ID,
                BuildConfig.MERCHANT_ID,
                BuildConfig.REBATE_PASSWORD,
                BuildConfig.REFUND_PASSWORD,
                BuildConfig.SHARED_SECRET,
                try {
                    Channel.valueOf(BuildConfig.CHANNEL)
                } catch (e: Exception) {
                    Channel.Ecom
                },
                BuildConfig.CHALLENGE_NOTIFICATION_URL,
                BuildConfig.MERCHANT_CONTACT_URL,
                BuildConfig.METHOD_NOTIFICATION_URL,
                try {
                    Secure3dVersion.valueOf(BuildConfig.SECURE_3D_VERSION)
                } catch (e: Exception) {
                    Secure3dVersion.ANY
                },
                try {
                    ShaHashType.valueOf(BuildConfig.SHA_HASH_TYPE)
                } catch (e: Exception) {
                    ShaHashType.SHA1
                },
                BuildConfig.API_KEY,
            )
        }
    }
}
