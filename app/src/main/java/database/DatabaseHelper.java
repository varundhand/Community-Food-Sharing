package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import models.FoodItem;
import models.Request;
import models.RequestStatus;
import models.User;
import models.UserType;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodSharing.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
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

    // food_items (donation_items in the ERD)
    public static final String COL_FOOD_ITEM_NAME = "name";
    public static final String COL_FOOD_ITEM_DONOR_ID = "donor_id";
    public static final String COL_FOOD_ITEM_CATEGORY_NAME = "category_name"; // enum name
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

        // Note: You will add the FoodItems table here next [cite: 34]
        String CREATE_FOOD_ITEMS_TABLE = "CREATE TABLE " + TABLE_FOOD_ITEMS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FOOD_ITEM_NAME + " TEXT,"
                + COL_FOOD_ITEM_DONOR_ID + " INTEGER,"
                + COL_FOOD_ITEM_CATEGORY_NAME + " TEXT,"
                + COL_FOOD_ITEM_QUANTITY + " TEXT,"
                + COL_FOOD_ITEM_EXPIRY_DATE + " TEXT," // 'YYYY-MM-DD'
                + COL_FOOD_ITEM_AVAILABLE_FROM + " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_AVAILABLE_TO + " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_IS_FREE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_PRICE_CENTS + " INTEGER," // cents
                + COL_FOOD_ITEM_IS_PICKUP_AVAILABLE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE + " INTEGER," // 1: TRUE
                + COL_FOOD_ITEM_IMG_KEY + " TEXT,"
                + COL_FOOD_ITEM_ADDED_AT + " INTEGER," // epoch seconds
                + COL_FOOD_ITEM_COMPLETED_AT + " INTEGER," // epoch seconds
                + "FOREIGN KEY(" + COL_FOOD_ITEM_DONOR_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID
                + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_FOOD_ITEMS_TABLE);

        // requests table
        String CREATE_REQUESTS_TABLE = "CREATE TABLE " + TABLE_REQUESTS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_REQUESTS_FOOD_ITEM_ID + " INTEGER,"
                + COL_REQUESTS_RECIPIENT_ID + " INTEGER,"
                + COL_REQUESTS_DUE + " INTEGER," // epoch seconds
                + COL_REQUESTS_STATUS + " TEXT,"
                + COL_REQUESTS_REQUESTED_AT + " INTEGER," // epoch seconds
                + "FOREIGN KEY(" + COL_REQUESTS_FOOD_ITEM_ID + ") REFERENCES " + TABLE_FOOD_ITEMS + "(" + COL_ID
                + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + COL_REQUESTS_RECIPIENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID
                + ") ON DELETE CASCADE ON UPDATE CASCADE"
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
                + "FOREIGN KEY(" + COL_REMINDERS_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID
                + ") ON DELETE CASCADE ON UPDATE CASCADE,"
                + "FOREIGN KEY(" + COL_REMINDERS_REQUEST_ID + ") REFERENCES " + TABLE_REQUESTS + "(" + COL_ID
                + ") ON DELETE CASCADE ON UPDATE CASCADE"
                + ")";
        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        onCreate(db);
    }

    // users related methods (delimiter for avoiding conflicts)

    // Basic Registration Method [cite: 28]
    public boolean registerUser(String name, String email, String password, String phone, String address, String post,
            UserType type, String imageKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_PHONE, phone);
        values.put(COL_USER_ADDRESS, address);
        values.put(COL_USER_POSTAL_CODE, post);
        values.put(COL_USER_TYPE, type.name());
        values.put(COL_USER_IMG_KEY, imageKey);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // getUser by userId
    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_ID + "=?",
                new String[] { String.format("%d", userId) });

        if (cursor.getCount() != 1) {
            return null;
        }
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(COL_USER_NAME));
        String email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndex(COL_USER_PHONE));
        String postalCode = cursor.getString(cursor.getColumnIndex(COL_USER_POSTAL_CODE));
        String address = cursor.getString(cursor.getColumnIndex(COL_USER_ADDRESS));
        String imgKey = cursor.getString(cursor.getColumnIndex(COL_USER_IMG_KEY));
        String typeStr = cursor.getString(cursor.getColumnIndex(COL_USER_TYPE));
        UserType userType = UserType.valueOf(typeStr); // TODO: handle exception (invalid string)

        return new User(id, name, email, phone, address, postalCode, userType, imgKey);
    }

    // getUser by email and pass
    public User getUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[] { email, password });

        if (cursor.getCount() != 1) {
            return null;
        }
        cursor.moveToFirst();
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(COL_USER_NAME));
        email = cursor.getString(cursor.getColumnIndex(COL_USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndex(COL_USER_PHONE));
        String postalCode = cursor.getString(cursor.getColumnIndex(COL_USER_POSTAL_CODE));
        String address = cursor.getString(cursor.getColumnIndex(COL_USER_ADDRESS));
        String imgKey = cursor.getString(cursor.getColumnIndex(COL_USER_IMG_KEY));
        String typeStr = cursor.getString(cursor.getColumnIndex(COL_USER_TYPE));
        UserType userType = UserType.valueOf(typeStr); // TODO: handle exception (invalid string)

        return new User(id, name, email, phone, address, postalCode, userType, imgKey);
    }

    // Basic Login Method [cite: 29]
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[] { email, password });
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // add food items related methods below (delimiter for avoiding conflicts)

    public boolean saveFoodItem(int donorId, String name, String category, String quantity,
            String expiry, Instant availableFrom, Instant availableTo, boolean isFree,
            int priceCents, boolean isPickupAvailable, boolean isDeliveryAvailable,
            String imageKey) {
        // datetime is stored as epoch seconds
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FOOD_ITEM_NAME, name);
        values.put(COL_FOOD_ITEM_DONOR_ID, donorId);
        values.put(COL_FOOD_ITEM_CATEGORY_NAME, category);
        values.put(COL_FOOD_ITEM_QUANTITY, quantity);
        values.put(COL_FOOD_ITEM_EXPIRY_DATE, expiry);
        if (availableFrom != null) {
            values.put(COL_FOOD_ITEM_AVAILABLE_FROM, availableFrom.getEpochSecond());
        }
        if (availableFrom != null) {
            values.put(COL_FOOD_ITEM_AVAILABLE_TO, availableTo.getEpochSecond());
        }
        values.put(COL_FOOD_ITEM_IS_FREE, isFree);
        values.put(COL_FOOD_ITEM_PRICE_CENTS, priceCents);
        values.put(COL_FOOD_ITEM_IS_PICKUP_AVAILABLE, isPickupAvailable);
        values.put(COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE, isDeliveryAvailable);
        values.put(COL_FOOD_ITEM_IMG_KEY, imageKey);

        Instant now = Instant.now();
        long epochSecs = now.getEpochSecond(); // 4bits (sqlite integer can handle it)
        values.put(COL_FOOD_ITEM_ADDED_AT, epochSecs);

        long result = db.insert(TABLE_FOOD_ITEMS, null, values);
        return result != -1;
    }

    public boolean saveFoodItem(FoodItem item) {
        if (item == null) return false;
        if (item.getId() <= 0) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // update all fields for simplicity
        values.put(COL_FOOD_ITEM_NAME, item.getName());
        values.put(COL_FOOD_ITEM_DONOR_ID, item.getDonorId());
        values.put(COL_FOOD_ITEM_CATEGORY_NAME, item.getCategory());
        values.put(COL_FOOD_ITEM_QUANTITY, item.getQuantity());
        values.put(COL_FOOD_ITEM_EXPIRY_DATE, item.getExpiry());
        if (item.getAvailableFrom() != null) {
            values.put(COL_FOOD_ITEM_AVAILABLE_FROM, item.getAvailableFrom().toInstant().getEpochSecond());
        } else {
            // nullify if the value is not provided
            values.putNull(COL_FOOD_ITEM_AVAILABLE_FROM);
        }
        if (item.getAvailableTo() != null) {
            values.put(COL_FOOD_ITEM_AVAILABLE_TO, item.getAvailableTo().toInstant().getEpochSecond());
        }
        values.put(COL_FOOD_ITEM_IS_FREE, item.isFree() ? 1 : 0);
        values.put(COL_FOOD_ITEM_PRICE_CENTS, item.getPriceCents());
        values.put(COL_FOOD_ITEM_IS_PICKUP_AVAILABLE, item.isPickupAvailable() ? 1 : 0);
        values.put(COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE, item.isDeliveryAvailable() ? 1 : 0);
        values.put(COL_FOOD_ITEM_IMG_KEY, item.getImageKey());

        Instant now = Instant.now();
        long epochSecs = now.getEpochSecond(); // 4bits (sqlite integer can handle it)
        values.put(COL_FOOD_ITEM_ADDED_AT, epochSecs);

        long result = db.update(TABLE_FOOD_ITEMS, values, COL_ID + "= ?", new String[]{ String.valueOf(item.getId()) });
        return result != -1;
    }

    public boolean deleteFoodItem(int foodItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FOOD_ITEMS, COL_ID + "= ?", new String[] { String.valueOf(foodItemId) });
        return result == 1;
    }

    public FoodItem getFoodItem(int foodItemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_FOOD_ITEMS + " WHERE " + COL_ID + "= ?", new String[] { String.valueOf(foodItemId) });

        if (cursor.getCount() != 1) {
            return null;
        }
        cursor.moveToFirst();

        int idIndex = cursor.getColumnIndex(COL_ID);
        int idName = cursor.getColumnIndex(COL_FOOD_ITEM_NAME);
        int idDonorId = cursor.getColumnIndex(COL_FOOD_ITEM_DONOR_ID);
        int idCategory = cursor.getColumnIndex(COL_FOOD_ITEM_CATEGORY_NAME);
        int idQuantity = cursor.getColumnIndex(COL_FOOD_ITEM_QUANTITY);
        int idExpiry = cursor.getColumnIndex(COL_FOOD_ITEM_EXPIRY_DATE);
        int idAvailableFrom = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_FROM);
        int idAvailableTo = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_TO);
        int idIsFree = cursor.getColumnIndex(COL_FOOD_ITEM_IS_FREE);
        int idPriceCents = cursor.getColumnIndex(COL_FOOD_ITEM_PRICE_CENTS);
        int idPickUpAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_PICKUP_AVAILABLE);
        int idDeliveryAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE);
        int idImageKey = cursor.getColumnIndex(COL_FOOD_ITEM_IMG_KEY);
        int idAddedAt = cursor.getColumnIndex(COL_FOOD_ITEM_ADDED_AT);
        int idCompletedAt = cursor.getColumnIndex(COL_FOOD_ITEM_COMPLETED_AT);

        int id = cursor.getInt(idIndex);
        String name = cursor.getString(idName);
        int donorId = cursor.getInt(idDonorId);
        String category = cursor.getString(idCategory);
        String quantity = cursor.getString(idQuantity);
        String expiry = cursor.getString(idExpiry);

        boolean isFree = cursor.getInt(idIsFree) == 1;
        int priceCents = cursor.getInt(idPriceCents);
        boolean isPickupAvailable = cursor.getInt(idPickUpAvailable) == 1;
        boolean isDeliveryAvailable = cursor.getInt(idDeliveryAvailable) == 1;
        String imageKey = cursor.getString(idImageKey);
        long addedAtEpochSecs = cursor.getLong(idAddedAt);

        ZonedDateTime availableFrom;
        if (cursor.isNull(idAvailableFrom)) availableFrom = null;
        else
            availableFrom = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableFrom)),
                    ZoneId.systemDefault());

        ZonedDateTime availableTo;
        if (cursor.isNull(idAvailableTo)) availableTo = null;
        else
            availableTo = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableTo)),
                    ZoneId.systemDefault());

        // Not null
        ZonedDateTime addedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(addedAtEpochSecs),
                ZoneId.systemDefault());

        ZonedDateTime completedAt;
        if (cursor.isNull(idCompletedAt)) completedAt = null;
        else
            completedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idCompletedAt)),
                    ZoneId.systemDefault());

        return new FoodItem(id, donorId, name, category, quantity,
                expiry, imageKey, availableFrom,
                availableTo, addedAt, completedAt, isFree, isPickupAvailable, isDeliveryAvailable, priceCents);
    }

    public ArrayList<FoodItem> listFoodItem(int donorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FOOD_ITEMS + " WHERE " + COL_FOOD_ITEM_DONOR_ID + "=?",
                new String[] { String.format("%d", donorId) });

        ArrayList<FoodItem> results = new ArrayList<>();

        if (cursor.getCount() == 0) {
            return results;
        }

        int idIndex = cursor.getColumnIndex(COL_ID);
        int idName = cursor.getColumnIndex(COL_FOOD_ITEM_NAME);
        int idCategory = cursor.getColumnIndex(COL_FOOD_ITEM_CATEGORY_NAME);
        int idQuantity = cursor.getColumnIndex(COL_FOOD_ITEM_QUANTITY);
        int idExpiry = cursor.getColumnIndex(COL_FOOD_ITEM_EXPIRY_DATE);
        int idAvailableFrom = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_FROM);
        int idAvailableTo = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_TO);
        int idIsFree = cursor.getColumnIndex(COL_FOOD_ITEM_IS_FREE);
        int idPriceCents = cursor.getColumnIndex(COL_FOOD_ITEM_PRICE_CENTS);
        int idPickUpAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_PICKUP_AVAILABLE);
        int idDeliveryAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE);
        int idImageKey = cursor.getColumnIndex(COL_FOOD_ITEM_IMG_KEY);
        int idAddedAt = cursor.getColumnIndex(COL_FOOD_ITEM_ADDED_AT);
        int idCompletedAt = cursor.getColumnIndex(COL_FOOD_ITEM_COMPLETED_AT);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(idName);
            String category = cursor.getString(idCategory);
            String quantity = cursor.getString(idQuantity);
            String expiry = cursor.getString(idExpiry);
            long availableFromEpochSecs = cursor.getLong(idAvailableFrom);
            long availableToEpochSecs = cursor.getLong(idAvailableTo);
            boolean isFree = cursor.getInt(idIsFree) == 1;
            int priceCents = cursor.getInt(idPriceCents);
            boolean isPickupAvailable = cursor.getInt(idPickUpAvailable) == 1;
            boolean isDeliveryAvailable = cursor.getInt(idDeliveryAvailable) == 1;
            String imageKey = cursor.getString(idImageKey);
            long addedAtEpochSecs = cursor.getLong(idAddedAt);

            ZonedDateTime availableFrom;
            if (cursor.isNull(idAvailableFrom)) availableFrom = null;
            else
                availableFrom = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableFrom)),
                        ZoneId.systemDefault());

            ZonedDateTime availableTo;
            if (cursor.isNull(idAvailableTo)) availableTo = null;
            else
                availableTo = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableTo)),
                        ZoneId.systemDefault());

            // Not null
            ZonedDateTime addedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(addedAtEpochSecs),
                    ZoneId.systemDefault());

            ZonedDateTime completedAt;
            if (cursor.isNull(idCompletedAt)) completedAt = null;
            else
                completedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idCompletedAt)),
                        ZoneId.systemDefault());

            FoodItem foodItem = new FoodItem(id, donorId, name, category, quantity,
                    expiry, imageKey, availableFrom,
                    availableTo, addedAt, completedAt, isFree, isPickupAvailable, isDeliveryAvailable, priceCents);
            results.add(foodItem);
        }

        return results;

    }

    public ArrayList<FoodItem> listNearbyFoodItems(String postalCode) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Search Food that are posted by donors who has the same first 3 digits of postalCode
        Cursor cursor = db.rawQuery("SELECT " + TABLE_FOOD_ITEMS + ".* FROM " + TABLE_FOOD_ITEMS +
                        " JOIN " + TABLE_USERS +
                        " ON " + TABLE_FOOD_ITEMS + "." + COL_FOOD_ITEM_DONOR_ID + " = " + TABLE_USERS + "." + COL_ID +
                        " WHERE " + TABLE_USERS + "." +COL_USER_TYPE + "=? AND " + TABLE_USERS + "." +COL_USER_POSTAL_CODE + " LIKE ?",
                new String[] { UserType.DONOR.name(), postalCode.substring(0, 3) + "%" });

        ArrayList<FoodItem> results = new ArrayList<>();

        if (cursor.getCount() == 0) {
            return results;
        }

        int idIndex = cursor.getColumnIndex(COL_ID);
        int idDonorId = cursor.getColumnIndex(COL_FOOD_ITEM_DONOR_ID);
        int idName = cursor.getColumnIndex(COL_FOOD_ITEM_NAME);
        int idCategory = cursor.getColumnIndex(COL_FOOD_ITEM_CATEGORY_NAME);
        int idQuantity = cursor.getColumnIndex(COL_FOOD_ITEM_QUANTITY);
        int idExpiry = cursor.getColumnIndex(COL_FOOD_ITEM_EXPIRY_DATE);
        int idAvailableFrom = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_FROM);
        int idAvailableTo = cursor.getColumnIndex(COL_FOOD_ITEM_AVAILABLE_TO);
        int idIsFree = cursor.getColumnIndex(COL_FOOD_ITEM_IS_FREE);
        int idPriceCents = cursor.getColumnIndex(COL_FOOD_ITEM_PRICE_CENTS);
        int idPickUpAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_PICKUP_AVAILABLE);
        int idDeliveryAvailable = cursor.getColumnIndex(COL_FOOD_ITEM_IS_DELIVERY_AVAILABLE);
        int idImageKey = cursor.getColumnIndex(COL_FOOD_ITEM_IMG_KEY);
        int idAddedAt = cursor.getColumnIndex(COL_FOOD_ITEM_ADDED_AT);
        int idCompletedAt = cursor.getColumnIndex(COL_FOOD_ITEM_COMPLETED_AT);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            int donorId = cursor.getInt(idDonorId);
            String name = cursor.getString(idName);
            String category = cursor.getString(idCategory);
            String quantity = cursor.getString(idQuantity);
            String expiry = cursor.getString(idExpiry);
            boolean isFree = cursor.getInt(idIsFree) == 1;
            int priceCents = cursor.getInt(idPriceCents);
            boolean isPickupAvailable = cursor.getInt(idPickUpAvailable) == 1;
            boolean isDeliveryAvailable = cursor.getInt(idDeliveryAvailable) == 1;
            String imageKey = cursor.getString(idImageKey);
            long addedAtEpochSecs = cursor.getLong(idAddedAt);

            ZonedDateTime availableFrom;
            if (cursor.isNull(idAvailableFrom)) availableFrom = null;
            else
                availableFrom = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableFrom)),
                        ZoneId.systemDefault());

            ZonedDateTime availableTo;
            if (cursor.isNull(idAvailableTo)) availableTo = null;
            else
                availableTo = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idAvailableTo)),
                        ZoneId.systemDefault());

            // Not null
            ZonedDateTime addedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(addedAtEpochSecs),
                    ZoneId.systemDefault());

            ZonedDateTime completedAt;
            if (cursor.isNull(idCompletedAt)) completedAt = null;
            else
                completedAt = ZonedDateTime.ofInstant(Instant.ofEpochSecond(cursor.getLong(idCompletedAt)),
                        ZoneId.systemDefault());

            FoodItem foodItem = new FoodItem(id, donorId, name, category, quantity,
                    expiry, imageKey, availableFrom,
                    availableTo, addedAt, completedAt, isFree, isPickupAvailable, isDeliveryAvailable, priceCents);
            results.add(foodItem);
        }

        return results;

    }

    // add requests related methods below (delimiter for avoiding conflicts)
    public boolean createRequest(int foodItemId, int recipientId, Instant due, RequestStatus status, Instant requestedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REQUESTS_FOOD_ITEM_ID, foodItemId);
        values.put(COL_REQUESTS_RECIPIENT_ID, recipientId);
        if (due != null) {
            values.put(COL_REQUESTS_DUE, due.getEpochSecond());
        }
        values.put(COL_REQUESTS_STATUS, status.name());

        if (requestedAt != null) {
            values.put(COL_REQUESTS_REQUESTED_AT, requestedAt.getEpochSecond());
        } else {
            Instant now = Instant.now();
            values.put(COL_REQUESTS_REQUESTED_AT, now.getEpochSecond());
        }

        long result = db.insert(TABLE_REQUESTS, null, values);
        return result == 1;
    }

    public ArrayList<Request> getRequests(int foodItemId, int recipientId, Instant dueAfter, RequestStatus status) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_REQUESTS +
                        " WHERE " + COL_REQUESTS_FOOD_ITEM_ID + " = ?" +
                        " AND " + COL_REQUESTS_RECIPIENT_ID + " = ?" +
                        " AND " + COL_REQUESTS_DUE + " > ?" +
                        " AND " + COL_REQUESTS_STATUS + " = ?",
                new String[]{
                        String.valueOf(foodItemId),
                        String.valueOf(recipientId),
                        String.valueOf(dueAfter.getEpochSecond()),
                        status.name()
                });

        int idIndex = cursor.getColumnIndex(COL_ID);
        int idFoodItemId = cursor.getColumnIndex(COL_REQUESTS_FOOD_ITEM_ID);
        int idRecipientId = cursor.getColumnIndex(COL_REQUESTS_RECIPIENT_ID);
        int idDue = cursor.getColumnIndex(COL_REQUESTS_DUE);
        int idStatus = cursor.getColumnIndex(COL_REQUESTS_STATUS);
        int idRequestedAt = cursor.getColumnIndex(COL_REQUESTS_REQUESTED_AT);

        ArrayList<Request> ret = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            int retFoodItemId = cursor.getInt(idFoodItemId);
            int retRecipientId = cursor.getInt(idRecipientId);
            ZonedDateTime due = null;
            if (!cursor.isNull(idDue)) {
                Instant dueInstant = Instant.ofEpochSecond(cursor.getLong(idDue));
                due = ZonedDateTime.ofInstant(dueInstant, ZoneId.systemDefault());
            }
            String statusStr = cursor.getString(idStatus);
            RequestStatus retStatus = RequestStatus.valueOf(statusStr);
            ZonedDateTime requestedAt = null;
            if (!cursor.isNull(idRequestedAt)) {
                Instant requestedAtInstant = Instant.ofEpochSecond(cursor.getLong(idRequestedAt));
                requestedAt = ZonedDateTime.ofInstant(requestedAtInstant, ZoneId.systemDefault());
            }

            ret.add(new Request(id, retFoodItemId, retRecipientId, due, retStatus, requestedAt));
        }
        return ret;
    }

    // add reminders related methods below (delimiter for avoiding conflicts)

    // add methods that involves multiple tables below

}