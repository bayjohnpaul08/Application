package com.example.application.Products;

import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.DbHelper;
import com.example.application.GetManufacturerNameModel;
import com.example.application.Manufacturers.ManufacturersFragment;
import com.example.application.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ProductEditFragment extends Fragment implements View.OnClickListener {

    private ImageView editProductImage;
    private EditText editProductName;
    private EditText editProductPrice;
    private AutoCompleteTextView editProductManufacturer;
    private TextView editProductDate;
    private TextView editProductId;
    private Button submitButton;
    private Button backButton;
    DbHelper db;
    ArrayList<GetManufacturerNameModel> nameList = new ArrayList<>();

    public ProductEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_edit, container, false);

        db = new DbHelper(getActivity());
        editProductImage = (ImageView) view.findViewById(R.id.editProductImage);
        editProductName = (EditText) view.findViewById(R.id.editProductName);
        editProductPrice = (EditText) view.findViewById(R.id.editProductPrice);
        editProductManufacturer = (AutoCompleteTextView) view.findViewById(R.id.editProductManufacturer);
        editProductId = (TextView) view.findViewById(R.id.editProductId);
        editProductDate = (TextView) view.findViewById(R.id.editProductDate);
        submitButton = (Button) view.findViewById(R.id.editProductButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        submitButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        editProductDate.setText(currentDate);

        Bundle bundle = getArguments();
        ProductModel productModel = bundle.getParcelable("data");

        editProductName.setText(productModel.getProductName());
        editProductPrice.setText(productModel.getProductPrice());
        editProductManufacturer.setText(productModel.getProductManufacturer());
        editProductId.setText(productModel.getProductId());
        Glide.with(getContext()).load(productModel.getProductImage()).placeholder(R.drawable.noimage).into(editProductImage);

        Cursor cursor = db.ManufacturerColumnName();
        while (cursor.moveToNext()) {
            GetManufacturerNameModel name = new GetManufacturerNameModel(cursor.getString(0));
            nameList.add(name);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, nameList);
        editProductManufacturer.setAdapter(arrayAdapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editProductButton:
                updateTheProduct();
                break;

            case R.id.backButton:
                backToProductFragment();
                break;
        }
    }

    private void updateTheProduct() {

        boolean isUpdate = db.updateProduct(
                editProductId.getText().toString().trim(),
                editProductName.getText().toString().trim(),
                editProductPrice.getText().toString().trim(),
                editProductManufacturer.getText().toString().trim(),
                editProductDate.getText().toString().trim());

        if(isUpdate == true){
            Toast.makeText(getActivity(), "Data is updated", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Data not updated", Toast.LENGTH_SHORT).show();
        }

        FragmentManager fragmentManager =getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductsFragment fragment = new ProductsFragment();

        fragmentTransaction.replace(R.id.container_fragment, fragment);
        fragmentTransaction.commit();
    }

    private void backToProductFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ProductsFragment fragment = new ProductsFragment();

        fragmentTransaction.replace(R.id.container_fragment, fragment);
        fragmentTransaction.commit();
    }
}