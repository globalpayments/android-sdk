package com.globalpayments.android.sdk.sample.common.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.globalpayments.android.sdk.utils.Strings

abstract class BaseComposeFragment : Fragment() {

    @Composable
    protected abstract fun ComposeFragmentScreen(
        showFragment: (containerViewId: Int, fragment: Fragment) -> Unit,
        closeFragment: () -> Unit
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragmentScreen(
                    this@BaseComposeFragment::show,
                    this@BaseComposeFragment::close
                )
            }
        }
    }

    protected fun close() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStackImmediate()
    }

    protected fun show(@IdRes containerViewId: Int, fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment)
            .addToBackStack(this.javaClass.simpleName + Strings.SPACE + fragment.javaClass.simpleName)
            .commit()
    }
}
