package com.example.khoavo.kk3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khoavo on 7/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "KhanhKy";

    // Contacts table name
    private static final String TABLE_ITEMS = "food";

    // Contacts Table Columns names

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CLEAN_NAME = "cleanName";
    private static final String KEY_PRICE = "price";

    private static DatabaseHelper myDb = null;
    private Context mContext;
    private static int count;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DatabaseHelper newInstance(Context context){
        if (myDb == null){
            myDb = new DatabaseHelper(context.getApplicationContext());
            count = 0;
        }
        return myDb;
    }

    public boolean isExisting(){
        if(myDb == null)
            return false;
        else
            return true;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_CLEAN_NAME + " TEXT,"
                + KEY_PRICE + " REAL" + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);

        // Create tables again
        onCreate(db);

    }


    public boolean insertData(Item item){
        SQLiteDatabase db = this.getWritableDatabase();

       // PRAGMA encoding = "UTF-8";

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,item.getName()); // Item Name
        values.put(KEY_CLEAN_NAME,item.getCleanName());
        values.put(KEY_PRICE,item.getPrice()); // Item Price

        Cursor cur = db.query(TABLE_ITEMS, null, "NAME = ? AND PRICE = ?",
                new String[] {item.getName(),Double.toString(item.getPrice())}
                , null, null, null, null);
        if (cur != null && cur.getCount()>0) {
            // duplicate found
            return false;
        }
        db.insert(TABLE_ITEMS,null,values);
        db.close();
        return true;

     }

     public int updateData(Item item){
         SQLiteDatabase db = this.getWritableDatabase();
         String id = Integer.toString(item.getID());
         ContentValues values = new ContentValues();
         values.put(KEY_NAME,item.getName());
         values.put(KEY_CLEAN_NAME,item.getCleanName());
         values.put(KEY_PRICE,item.getPrice());

       //  db.update(TABLE_ITEMS,values,"id = ?" + id, new String[] { String.valueOf(item.getName()) });
         int temp = db.update(TABLE_ITEMS, values,  KEY_NAME+ " =? ", new String[] { item.getName()});
         return temp;
     }

     public boolean isEmpty(SQLiteDatabase db){
         Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ITEMS, null);

         if(cursor.moveToNext())
             return false;

         else
             return true;
     }

    public Item getItem(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS, new String[] { KEY_ID,
                        KEY_NAME,KEY_CLEAN_NAME, KEY_PRICE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        //Item item = new Item(Integer.parseInt(cursor.getString(0)),
         //       cursor.getString(1), cursor.getString(2) , Double.parseDouble(cursor.getString(2)));
        Item item = new Item(Integer.parseInt(cursor.getString(0)),
                       cursor.getString(1), cursor.getString(2) , Double.parseDouble(cursor.getString(3)));
        return item;
    }

    public List<Item> getAllItems() {

        List<Item> itemList = new ArrayList<Item>();
        //Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Item item = new Item();
                item.setID(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setCleanName(cursor.getString(2));
                item.setPrice(Double.parseDouble(cursor.getString(3)));
                itemList.add(item);
            }
            while (cursor.moveToNext());
        }
        return itemList;
    }


    // Getting items Count
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    // Deleting single contact
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_NAME + " = ?",
                new String[] { String.valueOf(item.getName()) });
        db.close();
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS,null,null);
        //String clearDBQuery = "DELETE FROM "+TABLE_ITEMS;
        //db.execSQL(clearDBQuery);
    }

}
