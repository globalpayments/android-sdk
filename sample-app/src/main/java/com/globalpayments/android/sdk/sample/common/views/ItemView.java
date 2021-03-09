package com.globalpayments.android.sdk.sample.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.globalpayments.android.sdk.sample.R;

import static com.globalpayments.android.sdk.sample.common.views.Position.MIDDLE;
import static com.globalpayments.android.sdk.sample.common.views.Position.TOP;
import static com.globalpayments.android.sdk.utils.Utils.isNotNullOrBlank;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

public class ItemView extends RelativeLayout {
    private TextView labelTextView;
    private TextView valueTextView;

    private Position position = MIDDLE;
    private Orientation orientation = Orientation.HORIZONTAL;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(orientation.getLayoutId(), this);
        labelTextView = findViewById(R.id.labelTextView);
        valueTextView = findViewById(R.id.valueTextView);
    }

    private void setPaddings() {
        int padding_16dp = getResources().getDimensionPixelSize(R.dimen.size_16dp);
        int padding_2dp = getResources().getDimensionPixelSize(R.dimen.size_2dp);

        int topPadding = 0;
        int bottomPadding = 0;

        switch (position) {
            case TOP:
                topPadding = padding_16dp;
                bottomPadding = padding_16dp;
                break;
            case SECOND:
            case BELOW_A_GROUP:
                topPadding = padding_16dp;
                break;
            case MIDDLE:
            case BOTTOM:
                topPadding = padding_2dp;
                break;
        }

        setPaddingRelative(padding_16dp, topPadding, padding_16dp, bottomPadding);
    }

    public void setAsGroupHeader() {
        labelTextView.setTextSize(16f);

        int padding_16dp = getResources().getDimensionPixelSize(R.dimen.size_16dp);
        setPaddingRelative(getPaddingStart(), padding_16dp, getPaddingEnd(), getPaddingBottom());
    }

    public void setAsGroupSubHeader() {
        labelTextView.setTextSize(16f);
    }

    private void setColors() {
        if (position == TOP) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            setTextColor(R.color.colorWhite);
        }
    }

    public void setLabel(@StringRes int label) {
        labelTextView.setText(label);
    }

    public void setValue(String value) {
        valueTextView.setText(value);
    }

    public void setValueOrHide(String value) {
        if (isNotNullOrBlank(value)) {
            showView(this);
            setValue(value);
        } else {
            hideView(this);
        }
    }

    public void setTextColor(@ColorRes int colorRes) {
        int color = ContextCompat.getColor(getContext(), colorRes);
        labelTextView.setTextColor(color);
        valueTextView.setTextColor(color);
    }

    public static ItemView create(Context context, @StringRes int label, ViewGroup parent) {
        return create(context, label, parent, MIDDLE, Orientation.HORIZONTAL);
    }

    public static ItemView create(Context context, @StringRes int label, ViewGroup parent, Position position) {
        return create(context, label, parent, position, Orientation.HORIZONTAL);
    }

    public static ItemView create(Context context, @StringRes int label, ViewGroup parent, Orientation orientation) {
        return create(context, label, parent, MIDDLE, orientation);
    }

    public static ItemView create(Context context,
                                  @StringRes int label,
                                  ViewGroup parent,
                                  Position position,
                                  Orientation orientation) {
        ItemView itemView = new ItemView(context);
        itemView.setOrientation(orientation);
        itemView.init();
        itemView.setPosition(position);
        itemView.setLabel(label);
        itemView.setPaddings();
        itemView.setColors();
        parent.addView(itemView);
        return itemView;
    }

    public enum Orientation {
        HORIZONTAL(R.layout.horizontal_item_view),
        VERTICAL(R.layout.vertical_item_view);

        private int layoutId;

        Orientation(@LayoutRes int layoutId) {
            this.layoutId = layoutId;
        }

        public int getLayoutId() {
            return layoutId;
        }
    }
}
