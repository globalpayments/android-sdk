package com.globalpayments.android.sdk.utils;

import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;

public class ViewUtils {

    public static void handleViewVisibility(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public static void handleViewsVisibility(boolean show, View... views) {
        for (View view : views) {
            handleViewVisibility(view, show);
        }
    }

    public static void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    public static void hideView(View view) {
        view.setVisibility(View.GONE);
    }

    public static void hideViews(View... views) {
        for (View view : views) {
            hideView(view);
        }
    }

    public static void showViews(View... views) {
        for (View view : views) {
            showView(view);
        }
    }

    public static String getEditTextValue(EditText editText) {
        Editable text = editText.getText();
        return text == null ? EMPTY : text.toString();
    }

    public static void hideAllViewsExcluding(ViewGroup viewGroup, View... views) {
        List<View> visibleViews = Arrays.asList(views);

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            boolean show = visibleViews.contains(child);
            handleViewVisibility(child, show);
        }
    }
}
