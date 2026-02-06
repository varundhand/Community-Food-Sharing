package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodSharing.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FOOD_ITEMS = "food_items";

    // Common Columns
    public static final String COL_ID = "id";

    // Users Table Columns
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email"; // Unique ID 
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_ADDRESS = "address";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL to create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USER_NAME + " TEXT,"
                + COL_USER_EMAIL + " TEXT UNIQUE,"
                + COL_USER_PASSWORD + " TEXT,"
                + COL_USER_PHONE + " TEXT,"
                + COL_USER_ADDRESS + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Note: You will add the FoodItems table here next [cite: 34]
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Basic Registration Method [cite: 28]
    public boolean registerUser(String name, String email, String password, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_ADDRESS, address);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Basic Login Method [cite: 29]
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}