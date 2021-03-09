package com.globalpayments.android.sdk.sample.common.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {
    private final List<T> itemList = new ArrayList<>();

    public void submitList(List<T> newItemList) {
        itemList.clear();
        itemList.addAll(newItemList);
        notifyDataSetChanged();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract VH getViewHolder(View view);

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public T getItem(int position) {
        return itemList.get(position);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getViewHolder(LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(getItem(position));
    }
}
