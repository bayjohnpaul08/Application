package com.example.application.Products;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.application.DbHelper;
import com.example.application.GetManufacturerNameModel;
import com.example.application.Manufacturers.ManufacturersFragment;
import com.example.application.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddProductsFragment extends Fragment implements View.OnClickListener {

    private TextView productDate;
    private EditText productName, productPrice;
    private AutoCompleteTextView productManufacturer;
    private Button productButtonImage, productAddButton;
    private ImageView productImageView;
    Uri selectedImageUri;
    Bitmap bitmap;
    DbHelper db;
    ArrayList<GetManufacturerNameModel> nameList = new ArrayList<>();

    public AddProductsFragment() {
        // Required empty public constructor
    }

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 3;
    private static final int REQUEST_CODE_SELECT_IMAGE = 4;

    String productUrlImage = "http://192.168.0.106/pictures/index.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_products, container, false);
        db = new DbHelper(getActivity());

        productDate = (TextView) view.findViewById(R.id.productDate);
        productName = (EditText) view.findViewById(R.id.productName);
        productPrice = (EditText) view.findViewById(R.id.productPrice);
        productManufacturer = (AutoCompleteTextView) view.findViewById(R.id.productManufacturer);
        productButtonImage = (Button) view.findViewById(R.id.productImage);
        productAddButton = (Button) view.findViewById(R.id.productAddButton);
        productImageView = (ImageView) view.findViewById(R.id.productImageView);
        productButtonImage.setOnClickListener(this);
        productAddButton.setOnClickListener(this);

        String currentDate = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()));
        productDate.setText(currentDate);

        Cursor cursor = db.ManufacturerColumnName();

        while (cursor.moveToNext()) {
            GetManufacturerNameModel name = new GetManufacturerNameModel(cursor.getString(0));
            nameList.add(name);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, nameList);
        productManufacturer.setAdapter(arrayAdapter);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.productImage:

                if(ContextCompat.checkSelfPermission(
                        getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    uploadProductImage();
                }

                break;

            case R.id.productAddButton:
                addNewProduct();
        }
    }

    private void addNewProduct() {
        if(productName.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "The name of Product is required.", Toast.LENGTH_SHORT).show();
        }else{

            boolean isInserted = db.addProduct(
                    productName.getText().toString().trim(),
                    productDate.getText().toString().trim(),
                    productDate.getText().toString().trim(),
                    Integer.valueOf(productPrice.getText().toString().trim()),
                    productManufacturer.getText().toString().trim(),
                    selectedImageUri.toString()
            );
            Log.d("test", String.valueOf(isInserted));

            if(isInserted){
                Toast.makeText(getActivity(), "Product Successfully Added.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Failed to Add.",Toast.LENGTH_SHORT).show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, productUrlImage, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    String imageData = imageToString(bitmap);
                    params.put("image", imageData);

                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);

            FragmentManager fragmentManager =getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ProductsFragment fragment = new ProductsFragment();

            fragmentTransaction.replace(R.id.container_fragment, fragment);
            fragmentTransaction.commit();
        }
    }

    private void uploadProductImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    // for request to access external storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadProductImage();
            } else{
                Toast.makeText(getActivity(), "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    // for displaying image after the grant access
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {

            selectedImageUri = data.getData();
            if(selectedImageUri != null){
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    productImageView.setImageBitmap(bitmap);

                }catch (Exception exception) {
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // converting image
    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}