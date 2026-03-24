package database;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import models.User;
import models.UserRegistrationForm;
import models.UserSession;

public class AuthHelper {
    final static String SHARED_PREF_KEY = "shared_pref_user_session";
    final static String SHARED_PREF_USER_ID = "user_id";
    final static String SHARED_PREF_LOGGED_IN_AT = "logged_in_at";

    public static UserSession getCurrentSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt(SHARED_PREF_USER_ID, -1);
        long loggedInAt = sharedPreferences.getLong(SHARED_PREF_LOGGED_IN_AT, -1);
        if (userId == -1 || loggedInAt == -1) {
            // invalid record, log out and return null
            logout(context);
            return null;
        }
        Instant i = Instant.ofEpochSecond(loggedInAt);
        ZonedDateTime loggedInAtDateTime = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return new UserSession(loggedInAtDateTime, userId);
    }

    public static User registerUserAndLogin(Context context, UserRegistrationForm form) {
        return null;
    }

    public static UserSession login(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Instant now = Instant.now();
        long nowEpochSecs = now.getEpochSecond();
        editor.putInt(SHARED_PREF_USER_ID, userId);
        editor.putLong(SHARED_PREF_LOGGED_IN_AT, nowEpochSecs);
        editor.apply();

        return new UserSession(ZonedDateTime.ofInstant(now, ZoneId.systemDefault()), userId);
    }

    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
