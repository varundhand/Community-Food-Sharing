package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.math.BigDecimal;
import java.time.Instant;

import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodCategory;
import utils.ImageServer;

public class NewFoodItemActivity extends AppCompatActivity {
    EditText inputName, inputQuantity, inputExpiry, inputAvailableFrom, inputAvailableTo, inputPrice;
    RadioButton rdFree, rdDiscounted, rdPickup, rdDelivery;
    AutoCompleteTextView spinnerFoodCategory;
    ImageView imgUploadPreview;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_food_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputName = findViewById(R.id.inputFoodName);
        inputQuantity = findViewById(R.id.inputFoodQuantity);
        inputExpiry = findViewById(R.id.inputFoodExpiry);
        inputAvailableFrom = findViewById(R.id.inputAvailableFrom);
        inputAvailableTo = findViewById(R.id.inputAvailableTo);
        inputPrice = findViewById(R.id.inputPrice);

        rdFree = findViewById(R.id.rdFree);
        rdDiscounted = findViewById(R.id.rdDiscounted);

        rdPickup = findViewById(R.id.rdPickup);
        rdDelivery = findViewById(R.id.rdDelivery);

        spinnerFoodCategory = findViewById(R.id.spinnerFoodCategory);
        imgUploadPreview = findViewById(R.id.imgUploadPreview);

        // Populate spinner from FoodCategory enum
        FoodCategory[] categories = FoodCategory.values();
        ArrayAdapter<FoodCategory> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        spinnerFoodCategory.setAdapter(adapter);

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        photoUri = uri;
                        ImageServer imgServer = new ImageServer(this);
                        Bitmap bitmap = imgServer.loadImage(uri);
                        if (bitmap == null) {
                            Log.d("PhotoPicker", "Invalid media selected");
                            photoUri = null;
                            imgUploadPreview.setImageResource(0);
                            return;
                        }
                        imgUploadPreview.setImageBitmap(bitmap);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });
    }

    public void pickPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void handleCreate(View view) {
        String foodName = inputName.getText().toString().trim();
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fix: Retrieve category from AutoCompleteTextView correctly
        String selectedCategoryText = spinnerFoodCategory.getText().toString();
        FoodCategory category = FoodCategory.NOT_SELECTED;
        for (FoodCategory fc : FoodCategory.values()) {
            if (fc.toString().equals(selectedCategoryText)) {
                category = fc;
                break;
            }
        }

        if (category == FoodCategory.NOT_SELECTED) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantity = inputQuantity.getText().toString().trim();
        if (quantity.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String expiry = inputExpiry.getText().toString().trim();
        
        // Basic parsing for availability (Note: Instant.parse expects ISO-8601)
        Instant availableFrom = null;
        try {
            String fromStr = inputAvailableFrom.getText().toString().trim();
            if (!fromStr.isEmpty()) availableFrom = Instant.parse(fromStr);
        } catch (Exception e) {
            Log.e("NewFoodItem", "Failed to parse availableFrom", e);
        }

        Instant availableTo = null;
        try {
            String toStr = inputAvailableTo.getText().toString().trim();
            if (!toStr.isEmpty()) availableTo = Instant.parse(toStr);
        } catch (Exception e) {
            Log.e("NewFoodItem", "Failed to parse availableTo", e);
        }
        
        boolean isFree = rdFree.isChecked();
        boolean isDiscounted = rdDiscounted.isChecked();

        int cents = 0;
        if (isDiscounted) {
            try {
                String priceStr = inputPrice.getText().toString().trim();
                BigDecimal decimal = new BigDecimal(priceStr);
                cents = decimal.multiply(new BigDecimal(100)).intValue();
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean isPickup = rdPickup.isChecked();
        boolean isDelivery = rdDelivery.isChecked();

        String imageKey = null;
        if (photoUri != null) {
            ImageServer imageServer = new ImageServer(this);
            imageKey = imageServer.saveImage(photoUri);
        }

        AuthHelper authHelper = new AuthHelper(this);
        if (authHelper.getCurrentUser() == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            return;
        }
        int currentUserId = authHelper.getCurrentUser().getId();

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            // Fix: Use category.name() to store enum constant string in DB
            boolean success = dbHelper.saveFoodItem(currentUserId, foodName, category.name(), quantity,
                    expiry, availableFrom, availableTo, isFree, cents, isPickup,
                    isDelivery, imageKey);
            if (success) {
                Toast.makeText(this, "Food Item Created", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, DonorHomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Database save failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("NewFoodItemActivity", "Error saving food item", e);
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
