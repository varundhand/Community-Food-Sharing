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
    public static final String TABLE_FOOD_CATEGORIES = "food_categories";
    public static final String TABLE_FOOD_ITEMS = "food_items";
    public static final String TABLE_REQUESTS = "requests";
    public static final String TABLE_REMINDERS = "reminders";

    // Common Columns
    public static final String COL_ID = "id";

    // Users Table Columns
    public static final String COL_USER_NAME = "name";
    public static final String COL_USER_EMAIL = "email"; // Unique ID 
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_ADDRESS = "address";
    public static final String COL_USER_POSTAL_CODE = "postal_code";
    public static final String COL_USER_IMG_KEY = "image_key";
    public static final String COL_USER_TYPE = "user_type";

    // food_categories (donation_categories in the ERD
    public static final String COL_FOOD_CATEGORY_NAME = "name";

    // food_items (donation_items in the ERD)
    public static final String COL_FOOD_ITEM_NAME = "name";
    public static final String COL_FOOD_ITEM_DONOR_ID = "donor_id";
    public static final String COL_FOOD_ITEM_CATEGORY_ID = "category_id";
    public static final String COL_FOOD_ITEM_QUANTITY = "quantity";
    public static final String COL_FOOD_ITEM_EXPIRY_DATE = "expiry_date";
    public static final String COL_FOOD_ITEM_AVAILABLE_FROM = "available_from";
    public static final String COL_FOOD_ITEM_AVAILABLE_TO = "available_to";
    public static final String COL_FOOD_ITEM_IS_FREE = "is_free";
    public static final String COL_FOOD_ITEM_PRICE_CENTS = "price_cents";
    public static final String COL_FOOD_ITEM_IS_PICKUP_AVAILABLE = "is_pickup_available";
    public static final String COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE = "is_delivery_available";
    public static final String COL_FOOD_ITEM_IMG_KEY = "image_key";
    public static final String COL_FOOD_ITEM_ADDED_AT = "added_at";
    public static final String COL_FOOD_ITEM_COMPLETED_AT = "completed_at";

    // requests
    public static final String COL_REQUESTS_FOOD_ITEM_ID = "food_item_id";
    public static final String COL_REQUESTS_RECIPIENT_ID = "recipient_id";
    public static final String COL_REQUESTS_DUE = "due";
    public static final String COL_REQUESTS_STATUS = "status";
    public static final String COL_REQUESTS_REQUESTED_AT = "requested_at";

    // reminders
    public static final String COL_REMINDERS_USER_ID = "user_id";
    public static final String COL_REMINDERS_REQUEST_ID = "request_id";
    public static final String COL_REMINDERS_DISPLAY_FROM = "display_from";
    public static final String COL_REMINDERS_READ_AT = "read_at";
    public static final String COL_REMINDERS_TITLE = "title";
    public static final String COL_REMINDERS_CONTENT = "content"; // "text" in the ERD
    public static final String COL_REMINDERS_DEEPLINK_TEXT = "deeplink_text";
    public static final String COL_REMINDERS_DEEPLINK = "deeplink";

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
                + COL_USER_ADDRESS + " TEXT,"
                + COL_USER_POSTAL_CODE + " TEXT,"
                + COL_USER_IMG_KEY + " TEXT,"
                + COL_USER_TYPE + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_FOOD_CATEGORIES_TABLE ="CREATE TABLE " + TABLE_FOOD_CATEGORIES + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FOOD_CATEGORY_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_FOOD_CATEGORIES_TABLE);

        // Note: You will add the FoodItems table here next [cite: 34]
        String CREATE_FOOD_ITEMS_TABLE = "CREATE TABLE " + TABLE_FOOD_ITEMS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FOOD_ITEM_NAME + " TEXT,"
                + COL_FOOD_ITEM_DONOR_ID + " INTEGER,"
                + COL_FOOD_ITEM_CATEGORY_ID + " INTEGER,"
                + COL_FOOD_ITEM_QUANTITY + " TEXT,"
                + COL_FOOD_ITEM_EXPIRY_DATE + " TEXT," // 'YYYY-MM-DD'
                + COL_FOOD_ITEM_AVAILABLE_FROM + " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_AVAILABLE_TO +  " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_IS_FREE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_PRICE_CENTS + " INTEGER," // cents
                + COL_FOOD_ITEM_IS_PICKUP_AVAILABLE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_IMG_KEY + " TEXT,"
                + COL_FOOD_ITEM_ADDED_AT +  " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_COMPLETED_AT + " INTEGER," // epoch seconds
                + "FOREIGN KEY(" + COL_FOOD_ITEM_DONOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + COL_FOOD_ITEM_CATEGORY_ID + ") REFERENCES " + TABLE_FOOD_CATEGORIES + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_FOOD_ITEMS_TABLE);

        // requests table
        String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + "("
                + COL_REQUESTS_FOOD_ITEM_ID + " INTEGER,"
                + COL_REQUESTS_RECIPIENT_ID + " INTEGER,"
                + COL_REQUESTS_DUE + " INTEGER," // epoch seconds
                + COL_REQUESTS_STATUS + " TEXT,"
                + COL_REQUESTS_REQUESTED_AT + " INTEGER," // epoch seconds
                + "FOREIGN KEY(" + COL_REQUESTS_FOOD_ITEM_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + COL_REQUESTS_RECIPIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_REQUESTS_TABLE);

        // reminders
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS + "("
                + COL_REMINDERS_USER_ID + " INTEGER,"
                + COL_REMINDERS_REQUEST_ID + " INTEGER,"
                + COL_REMINDERS_DISPLAY_FROM + " INTEGER," // UNIX EPOCH
                + COL_REMINDERS_READ_AT + " INTEGER," // UNIX EPOCH
                + COL_REMINDERS_TITLE + " TEXT,"
                + COL_REMINDERS_CONTENT + " TEXT,"
                + COL_REMINDERS_DEEPLINK_TEXT + " TEXT,"
                + COL_REMINDERS_DEEPLINK + " TEXT,"
                + "FOREIGN KEY(" + COL_REMINDERS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + COL_REMINDERS_REQUEST_ID + ") REFERENCES " + TABLE_REQUESTS + "(" + COL_ID + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        onCreate(db);
    }

    // Basic Registration Method [cite: 28]
    public boolean registerUser(String name, String email, String password, String phone, String address, String post, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_ADDRESS, address);
        values.put(COL_USER_POSTAL_CODE, post);
        values.put(COL_USER_TYPE, type);

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