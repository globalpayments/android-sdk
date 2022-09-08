package com.globalpayments.android.sdk.sample.utils

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <V : View> Fragment.bindView(@IdRes idRes: Int): ReadOnlyProperty<Fragment, V> =
    FragmentBinder(this) {
        it.requireView().findViewById(idRes)
    }

private class FragmentBinder<out V : View>(
    val fragment: Fragment,
    val initializer: (Fragment) -> V
) : ReadOnlyProperty<Fragment, V>, DefaultLifecycleObserver {

    private object Empty

    private var viewValue: Any = Empty

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment) { it.lifecycle.addObserver(this) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        if (Empty == viewValue) {
            viewValue = initializer(fragment)
        }
        return viewValue as V
    }

    override fun onDestroy(owner: LifecycleOwner) {
        viewValue = Empty
    }
}