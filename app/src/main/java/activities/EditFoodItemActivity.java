package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodCategory;
import models.FoodItem;
import utils.ImageServer;

public class EditFoodItemActivity extends AppCompatActivity {
    EditText inputName, inputQuantity, inputExpiry, inputAvailableFrom, inputAvailableTo, inputPrice;
    RadioButton rdFree, rdDiscounted;
    CheckBox chkPickUp, chkDelivery;
    Spinner spinnerCategory;
    ImageView imgUploadPreview;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;

    ArrayAdapter<FoodCategory> spinnerAdapter;

    FoodItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_food_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int foodItemId = extras.getInt("FoodItemId");
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            item = dbHelper.getFoodItem(foodItemId);
        }

        if (item == null) {
            // Go back to donor home if the foodId is invalid
            Toast.makeText(this, "Invalid Food", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            return;
        }

        // Happy Path
        inputName = findViewById(R.id.inputFoodName);
        inputQuantity = findViewById(R.id.inputFoodQuantity);
        inputExpiry = findViewById(R.id.inputFoodExpiry);
        inputAvailableFrom = findViewById(R.id.inputAvailableFrom);
        inputAvailableTo = findViewById(R.id.inputAvailableTo);
        inputPrice = findViewById(R.id.inputPrice);

        rdFree = findViewById(R.id.rdFree);
        rdDiscounted = findViewById(R.id.rdDiscounted);

        chkPickUp = findViewById(R.id.chkPickup);
        chkDelivery = findViewById(R.id.chkDelivery);

        spinnerCategory = findViewById(R.id.spinnerFoodCategory);
        imgUploadPreview = findViewById(R.id.imgUploadPreview);

        // spinner from enum
        spinnerAdapter = new ArrayAdapter<FoodCategory>(this, android.R.layout.simple_spinner_item, FoodCategory.values());
        spinnerCategory.setAdapter(spinnerAdapter);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::setPreviewPhoto);
        setValues();
    }

    private void setPreviewPhoto(Uri uri) {
        if (uri != null) {
            photoUri = uri;
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(uri);
            setPreviewPhoto(bitmap);
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }

    private void setPreviewPhoto(String filename) {
        if (filename != null) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            setPreviewPhoto(bitmap);
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }

    private void setPreviewPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            Log.d("PhotoPicker", "Invalid media selected");
            photoUri = null;

            // clear image view
            // reference: https://stackoverflow.com/a/8243184
            imgUploadPreview.setImageResource(0);
            return;
        }

        // valid image
        imgUploadPreview.setImageBitmap(bitmap);
    }

    private void setValues() {
        if (item == null) return;

        inputName.setText(item.getName());
        // Setting value of spinner
        // reference: https://stackoverflow.com/a/11072595
        spinnerCategory.setSelection(spinnerAdapter.getPosition(item.getFoodCategory()));
        inputQuantity.setText(item.getQuantity());
        inputExpiry.setText(item.getExpiry());
        if (item.getAvailableFrom() != null) inputAvailableFrom.setText(item.getAvailableFrom().toString());
        if(item.getAvailableTo() != null) inputAvailableTo.setText(item.getAvailableTo().toString());

        setPreviewPhoto(item.getImageKey());

        if (item.isFree()) {
            rdFree.setChecked(true);
            rdDiscounted.setChecked(false);
        } else {
            rdFree.setChecked(false);
            rdDiscounted.setChecked(true);
        }

        inputPrice.setText(String.format("%.2f", item.getPriceDollar()));

        if (item.isPickupAvailable()) chkPickUp.setChecked(true);
        if (item.isDeliveryAvailable()) chkDelivery.setChecked(true);
    }

    public void pickPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void handleSave(View view) {
        String foodName = inputName.getText().toString();
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodCategory category = (FoodCategory) spinnerCategory.getSelectedItem();
        if (category == FoodCategory.NOT_SELECTED) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_SHORT).show();
            return;
        }
        String categoryName = category.name();

        String quantity = inputQuantity.getText().toString();
        if (quantity.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // allow empty for expiry, availability
        String expiry = inputExpiry.getText().toString();
        ZonedDateTime availableFrom;
        try {
            Instant instant = Instant.parse(inputAvailableFrom.getText().toString());
            availableFrom = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception e) {
            availableFrom = null;
        }

        ZonedDateTime availableTo;
        try {
            Instant instant = Instant.parse(inputAvailableFrom.getText().toString());
            availableTo = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception e) {
            availableTo = null;
        }
        boolean isFree = rdFree.isChecked();
        boolean isDiscounted = rdDiscounted.isChecked();
        if (isFree == isDiscounted) {
            Toast.makeText(this, "Please check free/discounted", Toast.LENGTH_SHORT).show();
            return;
        }

        int cents = 0;
        if (isDiscounted) {
            try {
                BigDecimal decimal = new BigDecimal(inputPrice.getText().toString());
                cents = decimal.multiply(new BigDecimal(100)).intValue();
                if (cents == 0) {
                    Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean isPickUpAvailable = chkPickUp.isChecked();
        boolean isDeliveryAvailable = chkDelivery.isChecked();

        if (!isPickUpAvailable && !isDeliveryAvailable) {
            Toast.makeText(this, "Please select Pick-Up and/or Delivery", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageKey = item.getImageKey();
        if (photoUri != null) {
            ImageServer imageServer = new ImageServer(this);
            imageKey = imageServer.saveImage(photoUri);
        }

        FoodItem updatedItem = new FoodItem(item.getId(), item.getDonorId(), foodName,
                categoryName, quantity, expiry, imageKey, availableFrom, availableTo,
                item.getAddedAt(), null, item.getCompletedAt(), isFree, isPickUpAvailable,
                isDeliveryAvailable, cents);

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            boolean success = dbHelper.saveFoodItem(updatedItem);
            if (success) {
                Toast.makeText(this, "Food Item Updated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
                startActivity(intent);
            } else {
                throw new IOException("DB save failed");
            }
        }
        catch (Exception e) {
            Log.d("NewFoodItemActivity.handleSave", e.toString());
            Toast.makeText(this, "DB Save failed", Toast.LENGTH_LONG).show();
            }
    }

    public void handleDelete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try (DatabaseHelper dbHelper = new DatabaseHelper(EditFoodItemActivity.this)) {
                    boolean result = dbHelper.deleteFoodItem(item.getId());
                    if (result) {
                        Toast.makeText(EditFoodItemActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditFoodItemActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setMessage("Delete?");
        builder.show();
    }
}