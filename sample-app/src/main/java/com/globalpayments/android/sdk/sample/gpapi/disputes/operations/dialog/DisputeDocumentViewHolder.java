package com.globalpayments.android.sdk.sample.gpapi.disputes.operations.dialog;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseViewHolder;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeDocument;

public class DisputeDocumentViewHolder extends BaseViewHolder<DisputeDocument> {
    private TextView tvFilename;
    private TextView tvType;

    private final OnItemClickListener onItemClickListener;

    public DisputeDocumentViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;
        initViews(itemView);
    }

    private void initViews(View itemView) {
        tvFilename = itemView.findViewById(R.id.tvFilename);
        tvType = itemView.findViewById(R.id.tvType);

        Button btRemove = itemView.findViewById(R.id.btRemove);
        btRemove.setOnClickListener(v -> onItemClickListener.onRemoveClicked(getLayoutPosition()));
    }

    @Override
    public void bind(DisputeDocument disputeDocument) {
        tvFilename.setText(disputeDocument.getFilename());
        tvType.setText(disputeDocument.getType().toString());
    }

    public interface OnItemClickListener {
        void onRemoveClicked(int itemPosition);
    }
}