package com.globalpayments.android.sdk.sample.utils

import com.global.api.builders.AuthorizationBuilder

fun AuthorizationBuilder.ifSatisfies(condition: () -> Boolean, block: AuthorizationBuilder.() -> AuthorizationBuilder): AuthorizationBuilder {
    return if (condition()) {
        this.block()
    } else {
        this
    }
}
