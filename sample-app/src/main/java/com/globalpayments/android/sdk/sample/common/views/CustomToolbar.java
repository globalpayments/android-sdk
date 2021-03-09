package com.globalpayments.android.sdk.sample.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatImageButton;

import com.globalpayments.android.sdk.sample.R;

public class CustomToolbar extends FrameLayout {

    public CustomToolbar(@NonNull Context context) {
        this(context, null);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomToolbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this);
    }

    public void setOnBackButtonListener(View.OnClickListener onBackButtonListener) {
        AppCompatImageButton backButton = findViewById(R.id.backButton);
        backButton.setVisibility(VISIBLE);
        backButton.setOnClickListener(onBackButtonListener);
    }

    public void setTitle(@StringRes int title) {
        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle.setText(title);
    }

    public AppCompatImageButton addAdditionalButton(@DrawableRes int drawableResource, View.OnClickListener onClickListener) {
        LinearLayout rootToolbarContainer = findViewById(R.id.rootToolbarContainer);
        AppCompatImageButton additionalButton = (AppCompatImageButton) LayoutInflater.from(getContext())
                .inflate(R.layout.toolbar_additional_button, rootToolbarContainer, false);

        additionalButton.setImageResource(drawableResource);
        additionalButton.setOnClickListener(onClickListener);

        rootToolbarContainer.addView(additionalButton);
        return additionalButton;
    }

}