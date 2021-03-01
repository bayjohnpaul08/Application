package com.example.application;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class AddProductsFragment extends Fragment {

    DbHelper db;
    TextView txtField;
    Button btn, upload;
    ProgressDialog pd;

    public AddProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_products, container, false);
        db = new DbHelper(getActivity());

        txtField = (TextView) view.findViewById(R.id.textField);
        btn = (Button) view.findViewById(R.id.btn);
        upload = (Button) view.findViewById(R.id.upld);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    downloadToSql();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToMysql();
            }
        });


        return view;
    }

    private void uploadToMysql() {

        OkHttpClient client = new OkHttpClient();
        String FILE_NAME = "file-name";

        String url = "http://192.168.0.107/practice/manufacturers/uploadToMysql";

        // progress dialog
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        JSONObject obj ;
        JSONArray jsonArray = new JSONArray();

        Cursor cursor = db.readAllData();
        while (cursor.moveToNext()){
            try {

                obj = new JSONObject();
                obj.put("manufacturersName", cursor.getString(1));
                obj.put("manufacturersCreated", cursor.getString(2));
                obj.put("manufacturersModified", cursor.getString(3));
                obj.put("manufacturersImage", cursor.getString(4));
                jsonArray.put(obj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", jsonArray.toString())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        pd.dismiss();

                        Log.i(TAG, e.getMessage());
                        txtField.setText("Failure !");
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        pd.dismiss();

                        txtField.setText(jsonArray.toString());
                        Toast.makeText(getActivity(), "Data successfully uploaded to backend", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    private void downloadToSql() throws JSONException  {

        String url1 = "http://192.168.0.107/practice/manufacturers/downloadToSql";
        OkHttpClient client = new OkHttpClient();

        // progress dialog
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        final Request request = new Request.Builder().url(url1).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();

                        Log.i(TAG, e.getMessage());
                        txtField.setText("Failure !");
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pd.dismiss();

                            JSONArray jsonArray = new JSONArray(response.body().string());

                            if(jsonArray != null && jsonArray.length() > 0){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    object.getString("name");
                                    object.getString("created");
                                    object.getString("modified");
                                    object.getString("image");

                                    DbHelper db = new DbHelper (getContext());
                                    // context of your activity or fragment
                                    try
                                    {
                                        db.addJson(object);

                                    } catch(Exception e) {
                                        Toast.makeText(getActivity(), "error: " + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                            }

                        Toast.makeText(getActivity(), "Data successfully download", Toast.LENGTH_SHORT).show();

                        }catch (IOException | JSONException e){
                            Toast.makeText(getActivity(), "error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}