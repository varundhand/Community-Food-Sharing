package models;

import java.time.ZonedDateTime;

public class Request {
    private int id, foodItemId, recipientId;
    private ZonedDateTime due;
    private RequestStatus status;
    private ZonedDateTime requested_at;

    public Request(int id, int foodItemId, int recipientId, ZonedDateTime due, RequestStatus status, ZonedDateTime requested_at) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.recipientId = recipientId;
        this.due = due;
        this.status = status;
        this.requested_at = requested_at;
    }

}
