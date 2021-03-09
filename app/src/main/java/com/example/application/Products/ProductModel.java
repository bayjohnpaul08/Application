package com.example.application.Products;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;

import com.example.application.Manufacturers.ManufacturersHelperClass;

import java.util.Objects;

public class ProductModel extends ManufacturersHelperClass implements Parcelable {

    private String productId;
    private String productName;
    private String productPrice;
    private String productManufacturer;
    private String productCreated;
    private String productModified;
    private String productImage;

    public ProductModel() {
    }

    public ProductModel(String productId, String productName, String productPrice, String productManufacturer, String productCreated, String productModified, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productManufacturer = productManufacturer;
        this.productCreated = productCreated;
        this.productModified = productModified;
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductManufacturer() {
        return productManufacturer;
    }

    public void setProductManufacturer(String productManufacturer) {
        this.productManufacturer = productManufacturer;
    }

    public String getProductCreated() {
        return productCreated;
    }

    public void setProductCreated(String productCreated) {
        this.productCreated = productCreated;
    }

    public String getProductModified() {
        return productModified;
    }

    public void setProductModified(String productModified) {
        this.productModified = productModified;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productManufacturer='" + productManufacturer + '\'' +
                ", productCreated='" + productCreated + '\'' +
                ", productModified='" + productModified + '\'' +
                ", productImage='" + productImage + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductModel that = (ProductModel) o;
        return productId.equals(that.productId) &&
                productName.equals(that.productName) &&
                productPrice.equals(that.productPrice) &&
                productManufacturer.equals(that.productManufacturer) &&
                productCreated.equals(that.productCreated) &&
                productModified.equals(that.productModified) &&
                productImage.equals(that.productImage);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, productPrice, productManufacturer, productCreated, productModified, productImage);
    }

    protected ProductModel(Parcel in) {
        productId = in.readString();
        productName = in.readString();
        productPrice = in.readString();
        productManufacturer = in.readString();
        productCreated = in.readString();
        productModified = in.readString();
        productImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(productName);
        dest.writeString(productPrice);
        dest.writeString(productManufacturer);
        dest.writeString(productCreated);
        dest.writeString(productModified);
        dest.writeString(productImage);
    }

    public static DiffUtil.ItemCallback<ProductModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ProductModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            return oldItem.getProductName().equals(newItem.getProductName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductModel oldItem, @NonNull ProductModel newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductModel> CREATOR = new Creator<ProductModel>() {
        @Override
        public ProductModel createFromParcel(Parcel in) {
            return new ProductModel(in);
        }

        @Override
        public ProductModel[] newArray(int size) {
            return new ProductModel[size];
        }
    };
}
