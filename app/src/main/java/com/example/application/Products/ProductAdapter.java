package com.example.application.Products;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.application.Manufacturers.ManufacturersHelperClass;
import com.example.application.R;

import java.util.ArrayList;

public class ProductAdapter extends ListAdapter<ProductModel, ProductAdapter.ProductModelViewHolder> {

    private final ProductAdapter.OnclickInterface onclickInterface;
    public ProductAdapter(@NonNull DiffUtil.ItemCallback<ProductModel> diffCallback, ProductAdapter.OnclickInterface onclickInterface) {
        super(diffCallback);
        this.onclickInterface = onclickInterface;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_product, parent, false);
        return new ProductAdapter.ProductModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductModelViewHolder holder, int position) {
        ProductModel model = getItem(position);
        if (model != null) {
            holder.bindTo(model);
        }

    }

    class ProductModelViewHolder extends RecyclerView.ViewHolder{
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productManufacturer;
        private final TextView productId;
        private final Button productViewButton;
        private final Button productEditButton;
        private final Button productDeleteButton;

        public ProductModelViewHolder(View view) {
            super(view);
            productImage = (ImageView) view.findViewById(R.id.productImage);
            productName = (TextView) view.findViewById(R.id.productName);
            productPrice = (TextView) view.findViewById(R.id.productPrice);
            productManufacturer = (TextView) view.findViewById(R.id.productManufacturer);
            productId = (TextView) view.findViewById(R.id.productId);
            productViewButton = (Button) view.findViewById(R.id.productViewButton);
            productEditButton = (Button) view.findViewById(R.id.productEditButton);
            productDeleteButton = (Button) view.findViewById(R.id.productDeleteButton);

            productViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onProductView(getAdapterPosition());
                }
            });

            productEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onProductEdit(getAdapterPosition());
                }
            });

            productDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onProductDelete(getAdapterPosition());
                }
            });

        }
        void bindTo(ProductModel productModel) {
            productId.setText(productModel.getProductId());
            productName.setText(productModel.getProductName());
            productPrice.setText(productModel.getProductPrice());
            productManufacturer.setText(productModel.getProductManufacturer());
            Glide.with(this.productImage).load(productModel.getProductImage()).placeholder(R.drawable.noimage).into(productImage);
        }

    }

    public interface OnclickInterface {
        public void onProductView(int position);
        public void onProductEdit(int position);
        public void onProductDelete(int position);
    }
}
