package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentResultListener;

import com.example.foodshare.R;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import database.DatabaseHelper;
import fragments.DatePickerFragment;
import fragments.TimePickerFragment;
import models.FoodCategory;
import models.FoodItem;
import utils.ImageServer;

public class EditFoodItemActivity extends AppCompatActivity {
    EditText inputName, inputQuantity, inputExpiry, inputAvailableFrom, inputAvailableTo, inputPrice;
    RadioButton rdFree, rdDiscounted, rdPickup, rdDelivery;
    AutoCompleteTextView spinnerCategory;
    ImageView imgUploadPreview;
    Button btnSave, btnDelete;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;

    ArrayAdapter<FoodCategory> spinnerAdapter;

    FoodItem item;

    private int pendingYear;
    private int pendingMonth;
    private int pendingDay;
    private EditText pendingAvailabilityInput;
    private final DateTimeFormatter availabilityDisplayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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
            Toast.makeText(this, "Invalid Food", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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

        btnSave = findViewById(R.id.btnSaveFood);
        btnDelete = findViewById(R.id.btnDeleteFood);

        spinnerCategory = findViewById(R.id.spinnerFoodCategory);
        imgUploadPreview = findViewById(R.id.imgUploadPreview);

        // spinner from enum
        spinnerAdapter = new ArrayAdapter<FoodCategory>(this, android.R.layout.simple_spinner_item,
                FoodCategory.values());
        spinnerCategory.setAdapter(spinnerAdapter);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::setPreviewPhoto);
        setValues();

        getSupportFragmentManager().setFragmentResultListener(
                DatePickerFragment.REQUEST_KEY,
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        pendingYear = result.getInt(DatePickerFragment.KEY_YEAR);
                        pendingMonth = result.getInt(DatePickerFragment.KEY_MONTH);
                        pendingDay = result.getInt(DatePickerFragment.KEY_DAY);

                        new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
                    }
                });

        getSupportFragmentManager().setFragmentResultListener(
                TimePickerFragment.REQUEST_KEY,
                this,
                new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        int hourOfDay = result.getInt(TimePickerFragment.KEY_HOUR_OF_DAY);
                        int minute = result.getInt(TimePickerFragment.KEY_MINUTE);

                        if (pendingAvailabilityInput == null) {
                            return;
                        }

                        LocalDateTime selectedDateTime = LocalDateTime.of(
                                pendingYear,
                                pendingMonth + 1,
                                pendingDay,
                                hourOfDay,
                                minute);

                        pendingAvailabilityInput.setText(availabilityDisplayFormatter.format(selectedDateTime));
                    }
                });

        inputAvailableFrom.setOnClickListener(v -> {
            pendingAvailabilityInput = inputAvailableFrom;
            new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
        });

        inputAvailableTo.setOnClickListener(v -> {
            pendingAvailabilityInput = inputAvailableTo;
            new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
        });

        if (item.isReserved()) {
            btnSave.setText("Update not Allowed (Reserved)");
            btnSave.setEnabled(false);
            btnDelete.setText("Delete not Allowed (Reserved)");
            btnDelete.setEnabled(false);
        }
    }

    private void setPreviewPhoto(Uri uri) {
        if (uri != null) {
            photoUri = uri;
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(uri);
            setPreviewPhoto(bitmap);
        }
    }

    private void setPreviewPhoto(String filename) {
        if (filename != null) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            setPreviewPhoto(bitmap);
        }
    }

    private void setPreviewPhoto(Bitmap bitmap) {
        if (bitmap == null) {
            photoUri = null;
            imgUploadPreview.setImageResource(0);
            findViewById(R.id.layoutUploadPlaceholder).setVisibility(View.VISIBLE);
            return;
        }
        imgUploadPreview.setImageBitmap(bitmap);
        findViewById(R.id.layoutUploadPlaceholder).setVisibility(View.GONE);
    }

    private void setValues() {
        if (item == null)
            return;

        inputName.setText(item.getName());
        spinnerCategory.setText(item.getFoodCategory().toString(), false);
        inputQuantity.setText(item.getQuantity());
        inputExpiry.setText(item.getExpiry());
        if (item.getAvailableFrom() != null)
            inputAvailableFrom.setText(availabilityDisplayFormatter.format(item.getAvailableFrom()));
        if (item.getAvailableTo() != null)
            inputAvailableTo.setText(availabilityDisplayFormatter.format(item.getAvailableTo()));

        setPreviewPhoto(item.getImageKey());

        if (item.isFree()) {
            rdFree.setChecked(true);
        } else {
            rdDiscounted.setChecked(true);
        }

        inputPrice.setText(String.format("%.2f", item.getPriceDollar().doubleValue()));

        // pickup and delivery are mutually exclusive
        if (item.isPickupAvailable())
            rdPickup.setChecked(true);
        else
            rdDelivery.setChecked(true);
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

        String selectedCategoryStr = spinnerCategory.getText().toString();
        FoodCategory category = FoodCategory.NOT_SELECTED;
        for(FoodCategory cat : FoodCategory.values()) {
            if(cat.toString().equals(selectedCategoryStr)) {
                category = cat;
                break;
            }
        }

        if (category == FoodCategory.NOT_SELECTED) {
            Toast.makeText(this, "Please Select a Category", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantity = inputQuantity.getText().toString();
        if (quantity.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String expiry = inputExpiry.getText().toString();

        ZonedDateTime availableFrom = parseAvailability(inputAvailableFrom);
        ZonedDateTime availableTo = parseAvailability(inputAvailableTo);

        boolean isFree = rdFree.isChecked();
        int cents = 0;
        if (!isFree) {
            try {
                BigDecimal decimal = new BigDecimal(inputPrice.getText().toString());
                cents = decimal.multiply(new BigDecimal(100)).intValue();
            } catch (Exception e) {
                Toast.makeText(this, "Please enter valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean isPickup = rdPickup.isChecked();
        boolean isDelivery = rdDelivery.isChecked();

        String imageKey = item.getImageKey();
        if (photoUri != null) {
            ImageServer imageServer = new ImageServer(this);
            imageKey = imageServer.saveImage(photoUri);
        }

        FoodItem updatedItem = new FoodItem(item.getId(), item.getDonorId(), foodName,
                category.name(), quantity, expiry, imageKey, availableFrom, availableTo,
                item.getAddedAt(), item.getReservedAt(), item.getCompletedAt(), isFree, isPickup,
                isDelivery, cents);

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            if (dbHelper.saveFoodItem(updatedItem)) {
                Toast.makeText(this, "Food Item Updated", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                throw new IOException("DB save failed");
            }
        } catch (Exception e) {
            Log.d("NewFoodItemActivity.handleSave", e.toString());
            Toast.makeText(this, "DB Save failed", Toast.LENGTH_LONG).show();
        }
    }

    public void handleDelete(View view) {
        new AlertDialog.Builder(this)
            .setMessage("Are you sure you want to delete this listing?")
            .setPositiveButton("Delete", (dialog, which) -> {
                try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
                    if (dbHelper.deleteFoodItem(item.getId())) {
                        Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditFoodItemActivity.this, DonorHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private ZonedDateTime parseAvailability(EditText input) {
        String value = input.getText() == null ? "" : input.getText().toString().trim();
        if (value.isEmpty()) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(value, availabilityDisplayFormatter);
            return localDateTime.atZone(ZoneId.systemDefault());
        } catch (Exception ignored) {
            return null;
        }
    }
}
