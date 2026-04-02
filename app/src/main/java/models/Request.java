package models;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    private int id, foodItemId, recipientId;
    private ZonedDateTime due;
    private RequestStatus status;
    private ZonedDateTime requested_at;

    // Associations
    private FoodItem foodItem;
    private User recipient;

    public Request(int id, int foodItemId, int recipientId, ZonedDateTime due, RequestStatus status, ZonedDateTime requested_at) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.recipientId = recipientId;
        this.due = due;
        this.status = status;
        this.requested_at = requested_at;
    }

    // variant with associations
    public Request(int id, FoodItem foodItem, User recipient, ZonedDateTime due, RequestStatus status, ZonedDateTime requested_at) {
        this.id = id;
        this.foodItemId = foodItem.getId();
        this.foodItem = foodItem;
        this.recipientId = recipient.getId();
        this.recipient = recipient;
        this.due = due;
        this.status = status;
        this.requested_at = requested_at;
    }

    public FoodItem getFoodItem() { return foodItem; }

    public User getRecipient() { return recipient; }

    public RequestStatus getStatus() { return status; }

    public ZonedDateTime getDue() { return due; }

    public String getFormattedDue() { return formatZonedDateTime(due); }

    private String formatZonedDateTime(ZonedDateTime date) {
        if (date == null) return "Not specified";
        // reference: https://www.baeldung.com/java-datetimeformatter#formatStyle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
