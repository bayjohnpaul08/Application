package com.example.application.Manufacturers;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

public class ManufacturersHelperClass implements Parcelable {

    private String id;
    private String manufacturersCode;
    private String manufacturersName;
    private String manufacturersCreated;
    private String manufacturersModified;
    private String manufacturersImage;

    public ManufacturersHelperClass() {

    }

    public ManufacturersHelperClass(String id, String manufacturersCode, String manufacturersName, String manufacturersCreated, String manufacturersModified, String manufacturersImage) {
        this.id = id;
        this.manufacturersCode = manufacturersCode;
        this.manufacturersName = manufacturersName;
        this.manufacturersCreated = manufacturersCreated;
        this.manufacturersModified = manufacturersModified;
        this.manufacturersImage = manufacturersImage;

    }

    public ManufacturersHelperClass(String s, String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    protected ManufacturersHelperClass(Parcel in) {
        id = in.readString();
        manufacturersName = in.readString();
        manufacturersCreated = in.readString();
        manufacturersModified = in.readString();
        manufacturersImage = in.readString();
    }

    public static final Creator<ManufacturersHelperClass> CREATOR = new Creator<ManufacturersHelperClass>() {
        @Override
        public ManufacturersHelperClass createFromParcel(Parcel in) {
            return new ManufacturersHelperClass(in);
        }

        @Override
        public ManufacturersHelperClass[] newArray(int size) {
            return new ManufacturersHelperClass[size];
        }
    };

    public String getManufacturersCode() {
        return manufacturersCode;
    }

    public void setManufacturersCode(String manufacturersCode) {
        this.manufacturersCode = manufacturersCode;
    }

    public String getManufacturersName() {
        return manufacturersName;
    }

    public void setManufacturersName(String manufacturersName) {
        this.manufacturersName = manufacturersName;
    }

    public String getManufacturersCreated() {
        return manufacturersCreated;
    }

    public void setManufacturersCreated(String manufacturersCreated) {
        this.manufacturersCreated = manufacturersCreated;
    }

    public String getManufacturersModified() {
        return manufacturersModified;
    }

    public void setManufacturersModified(String manufacturersModified) {
        this.manufacturersModified = manufacturersModified;
    }

    public String getManufacturersImage() {
        return manufacturersImage;
    }

    public void setManufacturersImage(String manufacturersImage) {
        this.manufacturersImage = manufacturersImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManufacturersHelperClass that = (ManufacturersHelperClass) o;
        return manufacturersName.equals(that.manufacturersName) &&
                manufacturersCreated.equals(that.manufacturersCreated) &&
                manufacturersModified.equals(that.manufacturersModified) &&
                manufacturersImage.equals(that.manufacturersImage);
    }

    public static DiffUtil.ItemCallback<ManufacturersHelperClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<ManufacturersHelperClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull ManufacturersHelperClass oldItem, @NonNull ManufacturersHelperClass newItem) {
            return oldItem.getManufacturersName().equals(newItem.getManufacturersName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ManufacturersHelperClass oldItem, @NonNull ManufacturersHelperClass newItem) {
            return oldItem.equals(newItem);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(manufacturersName, manufacturersCreated, manufacturersModified, manufacturersImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(manufacturersName);
        dest.writeString(manufacturersCreated);
        dest.writeString(manufacturersModified);
        dest.writeString(manufacturersImage);
    }
}
