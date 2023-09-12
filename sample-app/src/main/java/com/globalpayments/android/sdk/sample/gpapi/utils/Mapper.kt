package com.globalpayments.android.sdk.sample.gpapi.utils

import java.lang.reflect.Modifier
import kotlin.reflect.full.declaredMemberProperties

fun Any.mapNotNullFields(prefix: String = ""): List<Pair<String, String>> {
    val clazz =
        if (this.javaClass.superclass == null || this.javaClass.superclass.name == "java.lang.Object") this.javaClass else this.javaClass.superclass
    return clazz.declaredFields
        .map { it.isAccessible = true; it }
        .flatMap { member ->
            val fieldName = member.name
            val value = member.get(this) ?: return@flatMap emptyList()
            if (value is Iterable<*>) {
                value.flatMap { v -> v?.mapNotNullFields("$fieldName.") ?: emptyList() }
            } else if (value::class.declaredMemberProperties.isNotEmpty() && value::class != String::class && !value::class.java.isEnum) {
                value.mapNotNullFields("$fieldName.")
            } else if (!Modifier.isStatic(member.modifiers)) {
                listOf("$prefix$fieldName" to value.toString())
            } else {
                emptyList()
            }
        }
}
