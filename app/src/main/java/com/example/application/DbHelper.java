package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbHelper extends SQLiteOpenHelper {

    private Context context;
    public static final String DATABASE_NAME = "Application.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "manufacturersTable";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MANUFACTURER_NAME = "manufacturersName";
    private static final String COLUMN_MANUFACTURER_CREATED = "manufacturersCreated";
    private static final String COLUMN_MANUFACTURER_MODIFIED = "manufacturersModified";
    private static final String COLUMN_MANUFACTURER_IMAGE = "manufacturersImage";
    private static final String COLUMN_UPLOADED = "uploaded";

    JSONObject json = new JSONObject();

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MANUFACTURER_NAME + " TEXT, " +
                COLUMN_MANUFACTURER_CREATED + " TEXT," +
                COLUMN_MANUFACTURER_MODIFIED + " TEXT," +
                COLUMN_MANUFACTURER_IMAGE + " TEXT," +
                COLUMN_UPLOADED + " INTEGER DEFAULT 0)";
         db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addManufacturer(String manufacturersName, String manufacturersCreated, String manufacturersModified, String manufacturersImage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MANUFACTURER_NAME, manufacturersName);
        cv.put(COLUMN_MANUFACTURER_CREATED, manufacturersCreated);
        cv.put(COLUMN_MANUFACTURER_MODIFIED, manufacturersModified);
        cv.put(COLUMN_MANUFACTURER_IMAGE, manufacturersImage);

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

//    public String getNotUploaded(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_NAME + " WHERE uploaded = 0 ", null);
//
//        if(cursor.moveToFirst()){
//            do{
//                HashMap<String, String> map = new HashMap<String, String>();
//                map.put(COLUMN_ID, cursor.getString(0));
//                map.put(COLUMN_MANUFACTURER_NAME, cursor.getString(1));
//                map.put(COLUMN_MANUFACTURER_CREATED, cursor.getString(2));
//                map.put(COLUMN_MANUFACTURER_MODIFIED, cursor.getString(3));
//                map.put(COLUMN_MANUFACTURER_IMAGE, cursor.getString(4));
//
//            }while (cursor.moveToNext());
//        }
//    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public JSONArray readAllNotUploadedData() throws JSONException {
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE uploaded = 0 ";
        SQLiteDatabase db = this.getReadableDatabase();

        JSONArray jsonArray = new JSONArray();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

                JSONObject obj = new JSONObject();
                obj.put("manufacturersName", cursor.getString(1));
                obj.put("manufacturersCreated", cursor.getString(2));
                obj.put("manufacturersModified", cursor.getString(3));
                obj.put("manufacturersImage", cursor.getString(4));
                jsonArray.put(obj);

        }

        return jsonArray;
    }

    public boolean updateManufacturer(String id, String manufacturersName, String manufacturersModified){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MANUFACTURER_NAME, manufacturersName);
        cv.put(COLUMN_MANUFACTURER_MODIFIED, manufacturersModified);

        long result = db.update(TABLE_NAME, cv, " _id = ?", new String[] { id });
        if(result == -1){
            return false;
        }else {
            return true;
        }
    }

    public void updateAsUploaded(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " UPDATE manufacturersTable SET uploaded = 1 " ;

        Log.i("ATG", query);
        db.rawQuery(query, null);
    }

    public Integer deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    void addJson(JSONObject json) throws JSONException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MANUFACTURER_NAME, json.getString("name"));
        values.put(COLUMN_MANUFACTURER_CREATED, json.getString("created"));
        values.put(COLUMN_MANUFACTURER_MODIFIED, json.getString("modified"));
        values.put(COLUMN_MANUFACTURER_IMAGE, json.getString("image"));
        db.insert(TABLE_NAME, null, values);
        db.close();

    }
}
