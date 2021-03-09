package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.application.Manufacturers.ManufacturersHelperClass;
import com.example.application.Products.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Application.db";
    private static final int DATABASE_VERSION = 1;

    // TABLE NAMES
    private static final String TABLE_PRODUCT_NAME = "productTable";
    private static final String TABLE_MANUFACTURER_NAME = "manufacturersTable";

    // MANUFACTURER TABLE - COLUMNS
    private static final String COLUMN_MANUFACTURER_ID = "manufacturerId";
    private static final String COLUMN_MANUFACTURER_CODE = "manufacturersCode";
    private static final String COLUMN_MANUFACTURER_NAME = "manufacturersName";
    private static final String COLUMN_MANUFACTURER_CREATED = "manufacturersCreated";
    private static final String COLUMN_MANUFACTURER_MODIFIED = "manufacturersModified";
    private static final String COLUMN_MANUFACTURER_IMAGE = "manufacturersImage";
    private static final String COLUMN_MANUFACTURER_UPLOADED = "uploaded";


    // PRODUCT TABLE - COLUMNS
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_PRODUCT_NAME = "productName";
    private static final String COLUMN_PRODUCT_PRICE = "productPrice";
    private static final String COLUMN_PRODUCT_MANUFACTURER = "productManufacturer";
    private static final String COLUMN_PRODUCT_CREATED = "productCreated";
    private static final String COLUMN_PRODUCT_MODIFIED = "productModified";
    private static final String COLUMN_PRODUCT_IMAGE = "productImage";
    private static final String COLUMN_PRODUCT_UPLOADED = "productUploaded";

    private static final String CREATE_TABLE_MANUFACTURER = "CREATE TABLE " +
            TABLE_MANUFACTURER_NAME + " (" +
            COLUMN_MANUFACTURER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MANUFACTURER_CODE + " INTEGER, " +
            COLUMN_MANUFACTURER_NAME + " TEXT, " +
            COLUMN_MANUFACTURER_CREATED + " TEXT," +
            COLUMN_MANUFACTURER_MODIFIED + " TEXT," +
            COLUMN_MANUFACTURER_IMAGE + " TEXT," +
            COLUMN_MANUFACTURER_UPLOADED + " INTEGER DEFAULT 0)";

    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " +
            TABLE_PRODUCT_NAME + " (" +
            COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_PRODUCT_NAME + " TEXT, " +
            COLUMN_PRODUCT_PRICE + " INTEGER, " +
            COLUMN_PRODUCT_MANUFACTURER + " TEXT, " +
            COLUMN_PRODUCT_CREATED + " TEXT," +
            COLUMN_PRODUCT_MODIFIED + " TEXT," +
            COLUMN_PRODUCT_IMAGE + " TEXT," +
            COLUMN_PRODUCT_UPLOADED + " INTEGER DEFAULT 0)";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MANUFACTURER);
        db.execSQL(CREATE_TABLE_PRODUCT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANUFACTURER_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT_NAME);
        onCreate(db);
    }

    // CRUD OPERATIONS (CREATE, READ, UPDATE, DELETE, ETC)

    // MANUFACTURERS TABLE CRUD OPERATIONS

    public boolean addManufacturer(Integer manufacturersCode, String manufacturersName, String manufacturersCreated, String manufacturersModified, String manufacturersImage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MANUFACTURER_CODE, manufacturersCode);
        cv.put(COLUMN_MANUFACTURER_NAME, manufacturersName);
        cv.put(COLUMN_MANUFACTURER_CREATED, manufacturersCreated);
        cv.put(COLUMN_MANUFACTURER_MODIFIED, manufacturersModified);
        cv.put(COLUMN_MANUFACTURER_IMAGE, manufacturersImage);

        long result = db.insert(TABLE_MANUFACTURER_NAME,null, cv);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_MANUFACTURER_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public ArrayList<ProductModel> readAllRelated(ManufacturersHelperClass manufacturersHelperClass){
        String query = " SELECT " + " p. " + COLUMN_PRODUCT_ID + " , p. " + COLUMN_PRODUCT_NAME  + " , p. " + COLUMN_PRODUCT_PRICE + " , m. " + COLUMN_MANUFACTURER_NAME + " , p. " + COLUMN_PRODUCT_CREATED + " , p. " + COLUMN_PRODUCT_MODIFIED + " , p. " + COLUMN_PRODUCT_IMAGE
                + " FROM " + TABLE_MANUFACTURER_NAME + " m "
                + " INNER JOIN " + TABLE_PRODUCT_NAME + " p "
                + " ON m. " + COLUMN_MANUFACTURER_CODE + " = p. " + COLUMN_PRODUCT_MANUFACTURER
                + " WHERE p. " + COLUMN_PRODUCT_MANUFACTURER + " = " + manufacturersHelperClass.getManufacturersCode() ;

        ArrayList<ProductModel> relatedList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            ProductModel obj = new ProductModel();
            obj.setProductId(cursor.getString(0));
            obj.setProductName(cursor.getString(1));
            obj.setProductPrice(cursor.getString(2));
            obj.setProductCreated(cursor.getString(4));
            obj.setProductModified(cursor.getString(5));
            obj.setProductImage(cursor.getString(6));

            ManufacturersHelperClass manufacturers = new ManufacturersHelperClass();
            manufacturers.setManufacturersName(cursor.getString(3));

            relatedList.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return relatedList;
    }

    public JSONArray readAllNotUploadedData() throws JSONException {
        String query = "SELECT * FROM " + TABLE_MANUFACTURER_NAME + " WHERE uploaded = 0 ";
        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

                JSONObject obj = new JSONObject();
                obj.put("manufacturersCode", cursor.getString(1));
                obj.put("manufacturersName", cursor.getString(2));
                obj.put("manufacturersCreated", cursor.getString(3));
                obj.put("manufacturersModified", cursor.getString(4));
                obj.put("manufacturersImage", cursor.getString(5));
                jsonArray.put(obj);
        }

        return jsonArray;
    }

    public void updateAsUploaded(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MANUFACTURER_UPLOADED, 1);
        db.update(TABLE_MANUFACTURER_NAME, cv, COLUMN_MANUFACTURER_UPLOADED + " = 0 ", null);
    }

    public boolean updateManufacturer(String id, String manufacturersName, String manufacturersModified){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MANUFACTURER_NAME, manufacturersName);
        cv.put(COLUMN_MANUFACTURER_MODIFIED, manufacturersModified);

        long result = db.update(TABLE_MANUFACTURER_NAME, cv, " manufacturerId = ?", new String[] { id });
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Integer deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_MANUFACTURER_NAME, " manufacturerId=? ", new String[]{row_id});
    }

    void addJson(JSONObject json) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MANUFACTURER_CODE, json.getString("id"));
        values.put(COLUMN_MANUFACTURER_NAME, json.getString("name"));
        values.put(COLUMN_MANUFACTURER_CREATED, json.getString("created"));
        values.put(COLUMN_MANUFACTURER_MODIFIED, json.getString("modified"));
        values.put(COLUMN_MANUFACTURER_IMAGE, json.getString("image"));

        db.insert(TABLE_MANUFACTURER_NAME, null, values);
        db.close();
    }

    // PRODUCTS TABLE CRUD OPERATION

    public boolean addProduct(String productName, String productCreated, String productModified, Integer productPrice, String productManufacturer, String productImage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_NAME, productName);
        cv.put(COLUMN_PRODUCT_CREATED, productCreated);
        cv.put(COLUMN_PRODUCT_MODIFIED, productModified);
        cv.put(COLUMN_PRODUCT_PRICE, productPrice);
        cv.put(COLUMN_PRODUCT_MANUFACTURER, productManufacturer);
        cv.put(COLUMN_PRODUCT_IMAGE, productImage);

        long result = db.insert(TABLE_PRODUCT_NAME ,null, cv);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor productAllData(){
        String query = "SELECT p.*,m.manufacturersName FROM " + TABLE_PRODUCT_NAME + " p "
                + " INNER JOIN " + TABLE_MANUFACTURER_NAME + " m "
                + " ON m. " + COLUMN_MANUFACTURER_CODE + " = p. " + COLUMN_PRODUCT_MANUFACTURER ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public boolean updateProduct(String getProductId, String productName, String productPrice, String productModified, String productManufacturer){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PRODUCT_NAME, productName);
        cv.put(COLUMN_PRODUCT_PRICE, productPrice);
        cv.put(COLUMN_PRODUCT_MODIFIED, productModified);
        cv.put(COLUMN_PRODUCT_MANUFACTURER, productManufacturer);

        long result = db.update(TABLE_PRODUCT_NAME, cv, " productId = ?", new String[] { getProductId });
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Integer deleteOneProduct(String getProductId){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_PRODUCT_NAME, " productId=? ", new String[]{ getProductId });
    }

    public Cursor ManufacturerColumnName() {
        String query = " SELECT " + COLUMN_MANUFACTURER_CODE + " FROM " + TABLE_MANUFACTURER_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public JSONArray productReadAllNotUploadedData() throws JSONException {
        String query = "SELECT * FROM " + TABLE_PRODUCT_NAME + " WHERE productUploaded = 0 " ;
        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

            JSONObject obj = new JSONObject();
            obj.put("productName", cursor.getString(1));
            obj.put("productPrice", cursor.getString(2));
            obj.put("productManufacturer", cursor.getString(3));
            obj.put("productCreated", cursor.getString(4));
            obj.put("productModified", cursor.getString(5));
            obj.put("productImage", cursor.getString(6));
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    public void productUpdateAsUploaded(){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PRODUCT_UPLOADED, 1);
        db.update(TABLE_PRODUCT_NAME, cv, COLUMN_PRODUCT_UPLOADED + " = 0 ", null);
    }

    void productJson(JSONObject json) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, json.getString("product_name"));
        values.put(COLUMN_PRODUCT_PRICE, json.getString("price"));
        values.put(COLUMN_PRODUCT_CREATED, json.getString("created"));
        values.put(COLUMN_PRODUCT_MODIFIED, json.getString("modified"));
        values.put(COLUMN_PRODUCT_MANUFACTURER, json.getString("manufacturers_id"));
        values.put(COLUMN_PRODUCT_IMAGE, json.getString("image"));

        db.insert(TABLE_PRODUCT_NAME, null, values);
        db.close();
    }
}
