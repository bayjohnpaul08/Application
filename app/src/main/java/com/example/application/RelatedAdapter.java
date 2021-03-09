package com.example.application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.Manufacturers.Adapter;
import com.example.application.Manufacturers.ManufacturersHelperClass;

public class RelatedAdapter extends ListAdapter<RelatedProductsHelperClass, RelatedAdapter.RelatedProductsHelperClassViewHolder> {

    public RelatedAdapter(@NonNull DiffUtil.ItemCallback<RelatedProductsHelperClass> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public RelatedAdapter.RelatedProductsHelperClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_related_products, parent, false);
        return new RelatedAdapter.RelatedProductsHelperClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatedAdapter.RelatedProductsHelperClassViewHolder holder, int position) {
        RelatedProductsHelperClass relatedProductsHelperClass = getItem(position);
        if (relatedProductsHelperClass != null) {
            holder.bindTo(relatedProductsHelperClass);

        }
    }

    public class RelatedProductsHelperClassViewHolder extends RecyclerView.ViewHolder {
        TextView productName;

        public RelatedProductsHelperClassViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = (TextView) itemView.findViewById(R.id.relatedProductName);
        }

        void bindTo(RelatedProductsHelperClass relatedProductsHelperClass) {
            productName.setText(relatedProductsHelperClass.getProductName());
        }
    }
}
