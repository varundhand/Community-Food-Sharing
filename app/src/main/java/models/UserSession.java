package models;

import android.content.Context;

import java.time.ZonedDateTime;

public class UserSession {
    public UserSession(ZonedDateTime loggedInAt, int userId) {
        this.loggedInAt = loggedInAt;
        this.userId = userId;
    }

    final private ZonedDateTime loggedInAt;
    final private int userId;

    public ZonedDateTime getLoggedInAt() {
        return loggedInAt;
    }

    public int getUserId() {
        return userId;
    }
}
