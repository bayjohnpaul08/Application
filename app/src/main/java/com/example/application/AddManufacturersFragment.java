package com.example.application;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Button;
import android.widget.DatePicker;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddManufacturersFragment extends Fragment {

    private EditText manufacturersName;
    private ImageView manufacturersImage;
    private TextView date;
    private Button chooseImage, addButton;
    Bitmap bitmap;
    Uri selectedImageUri;
    DbHelper db;

    public AddManufacturersFragment() {
        // Required empty public constructor
    }

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2 ;

    String urlUpload = "http://192.168.0.105/pictures/index.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_manufacturers, container, false);

        db = new DbHelper(getActivity());

        manufacturersName = (EditText) view.findViewById(R.id.manufacturersName);
        manufacturersImage = (ImageView) view.findViewById(R.id.manufacturersImage);
        chooseImage = (Button) view.findViewById(R.id.chooseImage);
        addButton = (Button) view.findViewById(R.id.addButton);
        date = (TextView) view.findViewById(R.id.date);;

        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        date.setText(currentDate);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(
                        getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else {
                    selectImage();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
            }
        });



        return view;
    }

    private void addData() {
        if(manufacturersName.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "The name of Manufacturer is required.", Toast.LENGTH_SHORT).show();
        }else{

            boolean isInserted = db.addManufacturer(
                    manufacturersName.getText().toString().trim(),
                    date.getText().toString().trim(),
                    date.getText().toString().trim(),
                    selectedImageUri.toString()
            );

            if(isInserted == true){
                Toast.makeText(getActivity(), "Manufacturer Successfully Added.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "Failed to Add.",Toast.LENGTH_SHORT).show();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
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

            ManufacturersFragment fragment = new ManufacturersFragment();

            fragmentTransaction.replace(R.id.container_fragment, fragment);
            fragmentTransaction.commit();
        }
    }

    // upload image
    private void imageSave() {


    }


    private void selectImage() {
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
                selectImage();
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
                    manufacturersImage.setImageBitmap(bitmap);

                }catch (Exception exception) {
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

//    private String getPathFromUri(Uri contentUri) {
//        String filePath;
//        Cursor cursor = getActivity().getContentResolver()
//                .query(contentUri, null, null, null, null);
//
//        if (cursor == null) {
//            filePath = contentUri.getPath();
//        }else{
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex("_data");
//            filePath = cursor.getString(index);
//            cursor.close();
//        }
//        return filePath;
//    }

    // converting image
    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}