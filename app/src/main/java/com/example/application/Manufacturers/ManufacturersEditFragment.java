package com.example.application.Manufacturers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.application.DbHelper;
import com.example.application.R;

import java.text.DateFormat;
import java.util.Calendar;

public class ManufacturersEditFragment extends Fragment implements View.OnClickListener {

    private ImageView editImage;
    private EditText editName;
    private TextView editModified;
    private TextView editId;
    private Button submitButton;
    private Button backButton;
    ManufacturersHelperClass manufacturersHelperClass;
    DbHelper db;


    public ManufacturersEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_manufacturers_edit, container, false);

       db = new DbHelper(getActivity());

       editImage = (ImageView) view.findViewById(R.id.editImage);
       editId = (TextView) view.findViewById(R.id.editId);
       editName = (EditText) view.findViewById(R.id.editName);
       editModified = (TextView) view.findViewById(R.id.editModified);
       submitButton = (Button) view.findViewById(R.id.submitButton);
       backButton = (Button) view.findViewById(R.id.backButton);

       submitButton.setOnClickListener(this);
       backButton.setOnClickListener(this);

       Calendar calendar = Calendar.getInstance();
       String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
       editModified.setText(currentDate);

       // get all the data
       getData();

       return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitButton:
                updateData();
                break;

            case R.id.backButton:
                goBack();
                break;

        }
    }



    private void goBack() {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ManufacturersFragment fragment = new ManufacturersFragment();

                fragmentTransaction.replace(R.id.container_fragment, fragment);
                fragmentTransaction.commit();
    }

    private void updateData() {
                boolean isUpdate = db.updateManufacturer(
                        editId.getText().toString(),
                        editName.getText().toString(),
                        editModified.getText().toString()
                );

                if(isUpdate == true){
                    Toast.makeText(getActivity(), "Data is updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Data not updated", Toast.LENGTH_SHORT).show();
                }

                FragmentManager fragmentManager =getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ManufacturersFragment fragment = new ManufacturersFragment();

                fragmentTransaction.replace(R.id.container_fragment, fragment);
                fragmentTransaction.commit();
    }

    private void getData() {
        Bundle bundle = getArguments();
        manufacturersHelperClass = bundle.getParcelable("data");

        editId.setText(manufacturersHelperClass.getId());
        editName.setText(manufacturersHelperClass.getManufacturersName());
        editModified.setText(manufacturersHelperClass.getManufacturersModified());
        Glide.with(getContext()).load(manufacturersHelperClass.getManufacturersImage()).into(editImage);

    }


}