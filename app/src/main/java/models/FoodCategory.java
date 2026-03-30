package models;

public enum FoodCategory {
    // reference: https://stackoverflow.com/a/8619228
    NOT_SELECTED("Category not selected"), // null or invalid
    MEAT_AND_FISH("Meat and Fish"),
    VEGETABLES_AND_FRUITS("Vegetables and Fruits"),
    DAIRY("Dairy"),
    GRAIN("Grain");

    private String friendlyName;
    private FoodCategory(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String toString() {
        return friendlyName;
    }
}
