package com.example.application;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.application.Manufacturers.ManufacturersHelperClass;

public class RelatedProductsHelperClass {
    String productName;

    public RelatedProductsHelperClass(String productName) {
        this.productName = productName;
    }

    public RelatedProductsHelperClass() {

    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public static DiffUtil.ItemCallback<RelatedProductsHelperClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<RelatedProductsHelperClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull RelatedProductsHelperClass oldItem, @NonNull RelatedProductsHelperClass newItem) {
            return oldItem.getProductName().equals(newItem.getProductName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RelatedProductsHelperClass oldItem, @NonNull RelatedProductsHelperClass newItem) {
            return false;
        }
    };
}
