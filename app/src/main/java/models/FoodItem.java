package models;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FoodItem {
    int id, donorId;
    String name, category, quantity, expiry, imageKey;
    ZonedDateTime availableFrom, availableTo, addedAt, reservedAt, completedAt;
    boolean isFree, isPickupAvailable, isDeliveryAvailable;
    int priceCents;

    public FoodItem(int id, int donorId, String name, String category, String quantity,
                    String expiry, String imageKey, ZonedDateTime availableFrom,
                    ZonedDateTime availableTo, ZonedDateTime addedAt,ZonedDateTime reservedAt, ZonedDateTime completedAt,
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
        this.reservedAt = reservedAt;
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

    public FoodCategory getFoodCategory() { return FoodCategory.valueOf(category); }

    public String getQuantity() { return quantity; }

    public String getExpiry() { return expiry; }

    public String getImageKey() { return imageKey; }

    public ZonedDateTime getAvailableFrom() { return availableFrom; }

    public ZonedDateTime getAvailableTo() { return availableTo; }

    public ZonedDateTime getAddedAt() { return addedAt; }

    public ZonedDateTime getReservedAt() { return reservedAt; }
    public ZonedDateTime getCompletedAt() { return completedAt; }

    public boolean isFree() { return isFree; }

    public boolean isPickupAvailable() { return isPickupAvailable; }

    public boolean isDeliveryAvailable() { return isDeliveryAvailable; }

    public int getPriceCents() { return priceCents; }

    public BigDecimal getPriceDollar() {
        return BigDecimal.valueOf(getPriceCents()).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_EVEN);
    }

    private boolean isAvailable() {
        ZonedDateTime now = ZonedDateTime.now();
        boolean isAfter = availableFrom == null || now.isAfter(availableFrom);
        boolean isBefore = availableTo == null || now.isBefore(availableTo);

        return isAfter && isBefore;
    }

    public boolean isReserved() {
        return reservedAt != null;
    }

    public boolean isActive() {
        return completedAt == null && isAvailable();
    }

    private String formatZonedDateTime(ZonedDateTime date) {
        if (date == null) return "Not specified";
        // reference: https://www.baeldung.com/java-datetimeformatter#formatStyle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public String getFormattedAvailableFrom() {
        return formatZonedDateTime(availableFrom);
    }

    public String getFormattedAvailableTo() {
        return formatZonedDateTime(availableTo);
    }

    public String getFormattedAddedAt() {
        return formatZonedDateTime(addedAt);
    }

    public String getFormattedCompletedAt() {
        return formatZonedDateTime(completedAt);
    }
}
