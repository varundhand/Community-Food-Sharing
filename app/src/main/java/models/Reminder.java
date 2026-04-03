package models;

import java.time.ZonedDateTime;

public class Reminder {
    private int id, userId, requestId;
    private ZonedDateTime readAt;
    private String title, content;
    private ZonedDateTime addedAt;

    public Reminder(int id, int userId, int requestId, ZonedDateTime readAt, String title, String content, ZonedDateTime addedAt) {
        this.id = id;
        this.userId = userId;
        this.requestId = requestId;
        this.readAt = readAt;
        this.title = title;
        this.content = content;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }

    public int getUserId() {
        return userId;
    }

    public int getRequestId() {
        return requestId;
    }

    public ZonedDateTime getReadAt() {
        return readAt;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ZonedDateTime getAddedAt() {
        return addedAt;
    }
}
