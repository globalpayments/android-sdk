package com.globalpayments.android.sdk.sample.common.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public abstract class BaseDialogFragment extends AppCompatDialogFragment {

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
        initViews();
    }

    protected void initViews() {
        // No op
    }

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        useFullWidth();
    }

    public void useFullWidth() {
        setupDialogSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void setupDialogSize(int width, int height) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialogWindow.getAttributes());
                layoutParams.width = width;
                layoutParams.height = height;
                dialogWindow.setAttributes(layoutParams);
            }
        }
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        return requireView().findViewById(id);
    }
}
