package com.example.application;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class Adapter extends ListAdapter<ManufacturersHelperClass, Adapter.ManufacturersHelperClassViewHolder> {

    private final OnclickInterface onclickInterface;
    public Adapter(@NonNull DiffUtil.ItemCallback<ManufacturersHelperClass> diffCallback, OnclickInterface onclickInterface) {
        super(diffCallback);
        this.onclickInterface = onclickInterface;
    }

    @NonNull
    @Override
    public ManufacturersHelperClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_manufacturers, parent, false);
        return new ManufacturersHelperClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManufacturersHelperClassViewHolder holder, int position) {
        ManufacturersHelperClass manufacturersHelperClass = getItem(position);
        if (manufacturersHelperClass != null) {
            holder.bindTo(manufacturersHelperClass);

        }

    }

    class ManufacturersHelperClassViewHolder extends RecyclerView.ViewHolder{
        private final ImageView manufacturersImage;
        private final TextView manufacturersName;
        private final TextView manufacturersCreated;
        private final TextView manufacturersModified;
        private final TextView id;
        private final Button viewBtn;
        private final Button editBtn;
        private final Button deleteBtn;

        public ManufacturersHelperClassViewHolder(View view) {
            super(view);
            manufacturersImage = (ImageView) view.findViewById(R.id.manufacturersImage);
            manufacturersName = (TextView) view.findViewById(R.id.manufacturersName);
            manufacturersCreated = (TextView) view.findViewById(R.id.manufacturersCreated);
            manufacturersModified = (TextView) view.findViewById(R.id.manufacturersModified);
            id = (TextView) view.findViewById(R.id.id);
            viewBtn = (Button) view.findViewById(R.id.viewButton);
            editBtn = (Button) view.findViewById(R.id.editButton);
            deleteBtn = (Button) view.findViewById(R.id.deleteButton);

            viewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onView(getAdapterPosition());
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onEdit(getAdapterPosition());
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onclickInterface.onDelete(getAdapterPosition());
                }
            });

        }
        void bindTo(ManufacturersHelperClass manufacturersHelperClass) {
            id.setText(manufacturersHelperClass.getId());
            manufacturersName.setText(manufacturersHelperClass.getManufacturersName());
            manufacturersCreated.setText(manufacturersHelperClass.getManufacturersCreated());
            manufacturersModified.setText(manufacturersHelperClass.getManufacturersModified());
            Glide.with(this.manufacturersImage).load(manufacturersHelperClass.getManufacturersImage()).placeholder(R.drawable.noimage).into(manufacturersImage);
        }

    }

    interface OnclickInterface {
        public void onView(int position);
        public void onEdit(int position);
        public void onDelete(int position);
    }
}