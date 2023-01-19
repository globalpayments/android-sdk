package com.globalpayments.android.sdk.sample.common.base;

import static com.globalpayments.android.sdk.utils.Strings.SPACE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class BaseFragment extends Fragment {

    @LayoutRes
    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDependencies();
        initViews();
        initSubscriptions();
    }

    protected void initViews() {
        // No op
    }

    protected void initDependencies() {
        // No op
    }

    protected void initSubscriptions() {
        // No op
    }

    protected void close() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }

    protected void show(@IdRes int containerViewId, @NonNull Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(containerViewId, fragment)
                .addToBackStack(this.getClass().getSimpleName() + SPACE + fragment.getClass().getSimpleName())
                .commit();
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        return requireView().findViewById(id);
    }

    protected void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
