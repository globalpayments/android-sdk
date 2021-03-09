package com.globalpayments.android.sdk.sample.gpapi.disputes.operations.dialog;

import android.view.View;

import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseAdapter;
import com.globalpayments.android.sdk.sample.gpapi.disputes.operations.model.DisputeDocument;

public class DisputeDocumentsAdapter extends BaseAdapter<DisputeDocument, DisputeDocumentViewHolder> {
    OnItemClickListener onItemClickListener;

    protected DisputeDocumentsAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_dispute_document;
    }

    @Override
    protected DisputeDocumentViewHolder getViewHolder(View view) {
        return new DisputeDocumentViewHolder(view, itemPosition -> onItemClickListener.onRemoveClicked(getItem(itemPosition)));
    }

    public interface OnItemClickListener {
        void onRemoveClicked(DisputeDocument disputeDocument);
    }
}
