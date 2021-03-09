package com.example.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.application.Manufacturers.Adapter;
import com.example.application.Manufacturers.AddManufacturersFragment;
import com.example.application.Manufacturers.ManufacturersFragment;
import com.example.application.Manufacturers.ManufacturersHelperClass;
import com.example.application.Products.AddProductsFragment;
import com.example.application.Products.ProductViewFragment;
import com.example.application.Products.ProductsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    DbHelper db;
    ProgressDialog pd;
    ArrayList<ManufacturersHelperClass> manufacturersHelperClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DbHelper(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        // load default fragment
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new ManufacturersFragment());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.manufacturers) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ManufacturersFragment());
            fragmentTransaction.commit();
        }

        if (item.getItemId() == R.id.addManufacturers) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new AddManufacturersFragment());
            fragmentTransaction.commit();
        }

        if (item.getItemId() == R.id.products) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ProductsFragment());
            fragmentTransaction.commit();

        }

        if (item.getItemId() == R.id.addProducts) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new AddProductsFragment());
            fragmentTransaction.commit();
        }

        if (item.getItemId() == R.id.upload) {
          UploadToMysql();

        }

        if (item.getItemId() == R.id.download) {
            try {
                downloadToSql();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void UploadToMysql() {

        OkHttpClient client = new OkHttpClient();

        String manufacturerUrl = "http://192.168.0.106/practice/manufacturers/manufacturerToMysql";
        String productUrl = "http://192.168.0.106/practice/products/productToMysql";

        // progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();


        try {

            final Callback callback = new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();

                            Log.i("get", String.valueOf(response));
                            Toast.makeText(MainActivity.this, "Data successfully uploaded to backend", Toast.LENGTH_SHORT).show();
                            db.updateAsUploaded();
                            db.productUpdateAsUploaded();
                        }
                    });
                }
            };

            JSONArray manufacturersArray = db.readAllNotUploadedData();
            JSONArray productsArray = db.productReadAllNotUploadedData();


            // MANUFACTURER REQUEST
            RequestBody manufacturer = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("manufacturers", manufacturersArray.toString())
                    .build();

            Request manufacturerRequest = new Request.Builder()
                    .url(manufacturerUrl)
                    .post(manufacturer)
                    .build();

            // PRODUCT REQUEST
            RequestBody product = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("products", productsArray.toString())
                    .build();
            Log.i("prod", productsArray.toString());

            Request productRequest = new Request.Builder()
                    .url(productUrl)
                    .post(product)
                    .build();

            client.newCall(productRequest).enqueue(callback);
            client.newCall(manufacturerRequest).enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadToSql() throws JSONException  {

        String manufacturerUrl = "http://192.168.0.106/practice/manufacturers/downloadToSql";
        String productUrl = "http://192.168.0.106/practice/products/productToSqlite";

        OkHttpClient client = new OkHttpClient();

        // progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        final Request manufacturerRequest = new Request.Builder().url( manufacturerUrl).build();
        final Request productRequest = new Request.Builder().url(productUrl).build();

        client.newCall(manufacturerRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Log.i(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pd.dismiss();

                            JSONArray jsonArray = new JSONArray(response.body().string());
                            Log.i("get", jsonArray.toString());

                            if(jsonArray.length() > 0){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);
                                    object.getString("id");
                                    object.getString("name");
                                    object.getString("created");
                                    object.getString("modified");
                                    object.getString("image");

                                    try
                                    {
                                        db.addJson(object);
                                    } catch(Exception e) {
                                        Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            db.updateAsUploaded();
                            Toast.makeText(MainActivity.this, "Manufacturer successfully downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);

                        }catch (IOException | JSONException e){
                            Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        client.newCall(productRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Log.i(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pd.dismiss();

                            JSONArray jsonArray = new JSONArray(response.body().string());
                            Log.i("get", jsonArray.toString());

                            if(jsonArray != null && jsonArray.length() > 0){
                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject productObject = jsonArray.getJSONObject(i);
                                    productObject.getString("product_name");
                                    productObject.getString("price");
                                    productObject.getString("manufacturers_id");
                                    productObject.getString("created");
                                    productObject.getString("modified");
                                    productObject.getString("image");

                                    try
                                    {
                                        db.productJson(productObject);
                                    } catch(Exception e) {
                                        Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            db.productUpdateAsUploaded();
                            Toast.makeText(MainActivity.this, "product successfully downloaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intent);

                        }catch (IOException | JSONException e){
                            Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}