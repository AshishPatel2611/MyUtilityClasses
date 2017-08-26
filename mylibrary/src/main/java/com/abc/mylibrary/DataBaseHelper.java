package com.abc.mylibrary;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/package/databases/";
    private static String DB_NAME = "db";
    private static String TABLE_WHATS_NEW = "whats_new";

    private static String ID = "unique_id";
    private static String PRODUCT_ID = "product_id";
    private static String PRODUCT_NAME = "productName";
    private static String PRODUCT_DESCRIPTION = "description";
    private static String PRODUCT_IMAGE = "image";
    private static String PRODUCT_STATUS = "status";
    private static String PRODUCT_LINK = "link";

    private final Context myContext;
    private SQLiteDatabase SQLiteDatabase;

    static final int DB_VERSION = 1;


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String queryForChatTable = "CREATE TABLE IF NOT EXISTS " + TABLE_WHATS_NEW + "("
                + ID + " INTEGER PRIMARY KEY,"
                + PRODUCT_ID + " TEXT,"
                + PRODUCT_NAME + " TEXT,"
                + PRODUCT_DESCRIPTION + " TEXT,"
                + PRODUCT_IMAGE + " TEXT,"
                + PRODUCT_STATUS + " TEXT,"
                + PRODUCT_LINK + " TEXT)";

        //   Log.e(TAG, "onCreate queryForChatTable : " + queryForChatTable);
        //   Log.e(TAG, "onCreate queryForAppointment : " + queryForAppointment);
        sqLiteDatabase.execSQL(queryForChatTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        SQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WHATS_NEW);
        onCreate(sqLiteDatabase);
    }

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    // ---opens the database---
    public DataBaseHelper open() throws SQLException {
        SQLiteDatabase = getWritableDatabase();
        return this;
    }

    public void close() {
        try {
            SQLiteDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() {
        boolean dbExist = false;
        try {
            dbExist = checkDataBase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dbExist) {
            //     Log.e(TAG, "createDataBase: exist");
            //  Log.e("Database exist", "Database exist");
            //do nothing - database already exist
        } else {
            //      Log.e(TAG, "createDataBase: not exist");
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

        }
    }


/*
    public void AddWhatsNewProductItem(WhatsNewItem_bean whatsNewItem_bean) {

        SQLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, whatsNewItem_bean.id);
        values.put(PRODUCT_NAME, whatsNewItem_bean.productName);
        values.put(PRODUCT_DESCRIPTION, whatsNewItem_bean.description);
        values.put(PRODUCT_IMAGE, whatsNewItem_bean.image);
        values.put(PRODUCT_LINK, whatsNewItem_bean.link);
        values.put(PRODUCT_STATUS, whatsNewItem_bean.status);
        //  Log.e("dbhelper", "AddWhatsNewProductItem: values :" + values.toString());
        SQLiteDatabase.insert(TABLE_WHATS_NEW, null, values);

        SQLiteDatabase.close();

    }

    public void UpdateWhatsNewProductItem(WhatsNewItem_bean whatsNewItem_bean) {

        SQLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRODUCT_ID, whatsNewItem_bean.id);
        values.put(PRODUCT_NAME, whatsNewItem_bean.productName);
        values.put(PRODUCT_DESCRIPTION, whatsNewItem_bean.description);
        values.put(PRODUCT_IMAGE, whatsNewItem_bean.image);
        values.put(PRODUCT_LINK, whatsNewItem_bean.link);
        values.put(PRODUCT_STATUS, whatsNewItem_bean.status);
        //    Log.e("dbhelper", "UpdateWhatsNewProductItem: values :" + values.toString());

        SQLiteDatabase.update(TABLE_WHATS_NEW, values, PRODUCT_ID + "=" + whatsNewItem_bean.id, null);

        //  SQLiteDatabase.insert(TABLE_WHATS_NEW, null, values);

        SQLiteDatabase.close();

    }

    public void deleteAppointmentrecords(Context caller) {
        SQLiteDatabase = this.getReadableDatabase();
        SQLiteDatabase.execSQL("delete from " + TABLE_WHATS_NEW);
        SQLiteDatabase.close();
    }

    public WhatsNewItem_bean getProductforID(String id) {

        String sql = String.format("SELECT * FROM " + TABLE_WHATS_NEW
                + " WHERE " + PRODUCT_ID + "=" + id);
        SQLiteDatabase = this.getReadableDatabase();
        WhatsNewItem_bean bean = new WhatsNewItem_bean();
        Cursor mCursor = SQLiteDatabase.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                bean.id = mCursor.getString(mCursor.getColumnIndex(PRODUCT_ID));
                bean.productName = mCursor.getString(mCursor.getColumnIndex(PRODUCT_NAME));
                bean.description = mCursor.getString(mCursor.getColumnIndex(PRODUCT_DESCRIPTION));
                bean.image = mCursor.getString(mCursor.getColumnIndex(PRODUCT_IMAGE));
                bean.link = mCursor.getString(mCursor.getColumnIndex(PRODUCT_LINK));
                bean.status = mCursor.getString(mCursor.getColumnIndex(PRODUCT_STATUS));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        SQLiteDatabase.close();
        return bean;
    }

    public ArrayList<WhatsNewItem_bean> getProductList() {
        ArrayList<WhatsNewItem_bean> ProductList = new ArrayList<>();
        String sql = String.format("SELECT * FROM " + TABLE_WHATS_NEW);
        SQLiteDatabase = this.getReadableDatabase();
        Cursor mCursor = SQLiteDatabase.rawQuery(sql, null);
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                WhatsNewItem_bean bean = new WhatsNewItem_bean();
                bean.id = mCursor.getString(mCursor.getColumnIndex(PRODUCT_ID));
                bean.productName = mCursor.getString(mCursor.getColumnIndex(PRODUCT_NAME));
                bean.description = mCursor.getString(mCursor.getColumnIndex(PRODUCT_DESCRIPTION));
                bean.image = mCursor.getString(mCursor.getColumnIndex(PRODUCT_IMAGE));
                bean.link = mCursor.getString(mCursor.getColumnIndex(PRODUCT_LINK));
                bean.status = mCursor.getString(mCursor.getColumnIndex(PRODUCT_STATUS));
                ProductList.add(bean);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        SQLiteDatabase.close();
        return ProductList;
    }

*/

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            //  Log.e("db", "checkDataBase:opening db... ");
            String myPath = DB_PATH + DB_NAME;
            checkDB = android.database.sqlite.SQLiteDatabase.openDatabase(myPath, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }


}

