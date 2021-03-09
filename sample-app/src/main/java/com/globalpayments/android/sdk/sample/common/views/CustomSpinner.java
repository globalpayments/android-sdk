package com.globalpayments.android.sdk.sample.common.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    private final static String NOTHING_OPTION = "----";

    public CustomSpinner(@NonNull Context context) {
        super(context);
    }

    public CustomSpinner(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    public <T> void init(T[] objects) {
        init(Arrays.asList(objects));
    }

    public <T> void init(T[] objects, boolean addFirstEmptyItem) {
        init(Arrays.asList(objects), addFirstEmptyItem);
    }

    public <T> void init(T[] objects,
                         boolean addFirstEmptyItem,
                         OnItemSelectedListener<T> onItemSelectedListener) {
        init(Arrays.asList(objects), addFirstEmptyItem, onItemSelectedListener);
    }

    public <T> void init(List<T> objects) {
        init(objects, false);
    }

    public <T> void init(List<T> objects, boolean addFirstEmptyItem) {
        init(objects, addFirstEmptyItem, null);
    }

    public <T> void init(List<T> objects,
                         boolean addFirstEmptyItem,
                         OnItemSelectedListener<T> onItemSelectedListener) {
        List<Object> finalObjectList = new ArrayList<>();

        if (addFirstEmptyItem) {
            finalObjectList.add(NOTHING_OPTION);
        }

        finalObjectList.addAll(objects);

        ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, finalObjectList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (onItemSelectedListener != null) {
            setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Object selectedItem = parent.getItemAtPosition(position);
                    if (!NOTHING_OPTION.equals(selectedItem)) {
                        onItemSelectedListener.onItemSelected((T) selectedItem);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //No op
                }
            });
        }

        setAdapter(arrayAdapter);
    }

    public <T> T getSelectedOption() {
        Object selectedItem = super.getSelectedItem();
        return NOTHING_OPTION.equals(selectedItem) ? null : (T) selectedItem;
    }

    public void selectItem(Object object) {
        setSelection(((ArrayAdapter<Object>) getAdapter()).getPosition(object));
    }

    @FunctionalInterface
    public interface OnItemSelectedListener<T> {
        void onItemSelected(T t);
    }
}
