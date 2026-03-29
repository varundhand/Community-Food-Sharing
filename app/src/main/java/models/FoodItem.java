package models;

import java.time.ZonedDateTime;

public class FoodItem {
    int id, donorId;
    String name, category, quantity, expiry, imageKey;
    ZonedDateTime availableFrom, availableTo, addedAt, completedAt;
    boolean isFree, isPickupAvailable, isDeliveryAvailable;
    int priceCents;

    public FoodItem(int id, int donorId, String name, String category, String quantity,
                    String expiry, String imageKey, ZonedDateTime availableFrom,
                    ZonedDateTime availableTo, ZonedDateTime addedAt, ZonedDateTime completedAt,
                    boolean isFree, boolean isPickupAvailable, boolean isDeliveryAvailable,
                    int priceCents) {
        this.id = id;
        this.donorId = donorId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.expiry = expiry;
        this.imageKey = imageKey;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.addedAt = addedAt;
        this.completedAt = completedAt;
        this.isFree = isFree;
        this.isPickupAvailable = isPickupAvailable;
        this.isDeliveryAvailable = isDeliveryAvailable;
        this.priceCents = priceCents;
    }

    public int getId() { return id; }

    public int getDonorId() { return donorId; }

    public String getName() { return name; }

    public String getCategory() { return category; }

    public String getQuantity() { return quantity; }

    public String getExpiry() { return expiry; }

    public String getImageKey() { return imageKey; }

    public ZonedDateTime getAvailableFrom() { return availableFrom; }

    public ZonedDateTime getAvailableTo() { return availableTo; }

    public ZonedDateTime getAddedAt() { return addedAt; }

    public ZonedDateTime getCompletedAt() { return completedAt; }

    public boolean isFree() { return isFree; }

    public boolean isPickupAvailable() { return isPickupAvailable; }

    public boolean isDeliveryAvailable() { return isDeliveryAvailable; }

    public int getPriceCents() { return priceCents; }

    private boolean isAvailable() {
        ZonedDateTime now = ZonedDateTime.now();
        boolean isAfter = availableFrom == null || now.isAfter(availableFrom);
        boolean isBefore = availableTo == null || now.isBefore(availableTo);

        return isAfter && isBefore;
    }

    public boolean isActive() {
        return completedAt == null && isAvailable();
    }
}
