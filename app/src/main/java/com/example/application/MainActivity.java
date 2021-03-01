package com.example.application;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.HttpClientStack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
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
import okhttp3.MediaType;
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
    public static final String url = "http://localhost/pictures/connection.php";
    Adapter adapter;
    String getId, getName, getCreated, getModified, getImage;
    List<ManufacturersHelperClass> list = new ArrayList<>();
    ManufacturersHelperClass manufacturersHelperClass;

    DbHelper db;
    TextView txtField;
    Button btn, upload;
    ProgressDialog pd;

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
            uploadToMysql();
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

    private void uploadToMysql() {

        OkHttpClient client = new OkHttpClient();

        String url = "http://192.168.0.109/practice/manufacturers/uploadToMysql";

        // progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();


        try {

            JSONArray manufacturersArray = db.readAllNotUploadedData();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("manufacturers", manufacturersArray.toString())
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            pd.dismiss();

                            Log.i("ASDF", e.getMessage());
                            txtField.setText("Failure !");
                        }
                    });

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        Log.i("ASDF",response.body().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            pd.dismiss();

                            Toast.makeText(MainActivity.this, "Data successfully uploaded to backend", Toast.LENGTH_SHORT).show();
                            db.updateAsUploaded();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadToSql() throws JSONException  {

        String url1 = "http://192.168.0.109/practice/manufacturers/downloadToSql";
        OkHttpClient client = new OkHttpClient();

        // progress dialog
        pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        final Request request = new Request.Builder().url(url1).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
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
                runOnUiThread(new Runnable() {
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

                                    // context of your activity or fragment
                                    try
                                    {
                                        db.addJson(object);

                                    } catch(Exception e) {
                                        Toast.makeText(MainActivity.this, "error: " + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                            }

                            Toast.makeText(MainActivity.this, "Data successfully downloaded", Toast.LENGTH_SHORT).show();
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