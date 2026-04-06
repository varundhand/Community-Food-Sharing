package activities;

import android.app.DatePickerDialog;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentResultListener;

import com.example.foodshare.R;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.Instant;

import database.AuthHelper;
import database.DatabaseHelper;
import fragments.DatePickerFragment;
import fragments.TimePickerFragment;
import models.FoodCategory;
import utils.ImageServer;

public class NewFoodItemActivity extends AppCompatActivity {
    EditText inputName, inputQuantity, inputExpiry, inputAvailableFrom, inputAvailableTo, inputPrice;
    RadioButton rdFree, rdDiscounted, rdPickup, rdDelivery;
    AutoCompleteTextView spinnerFoodCategory;
    ImageView imgUploadPreview;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;

    private int pendingYear;
    private int pendingMonth;
    private int pendingDay;
    private EditText pendingAvailabilityInput;
    private final DateTimeFormatter availabilityDisplayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

        // spinner from enum
        FoodCategory[] categories = FoodCategory.values();

        // Create an ArrayAdapter using a standard Android dropdown layout
        ArrayAdapter<FoodCategory> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories);

        // Attach the adapter
        spinnerFoodCategory.setAdapter(adapter);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                photoUri = uri;
                ImageServer imgServer = new ImageServer(this);
                Bitmap bitmap = imgServer.loadImage(uri);
                if (bitmap == null) {
                    Log.d("PhotoPicker", "Invalid media selected");
                    photoUri = null;

                    // clear image view
                    imgUploadPreview.setImageResource(0);
                    return;
                }

                // valid image
                imgUploadPreview.setImageBitmap(bitmap);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

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
    }

    public void pickPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    public void handleCreate(View view) {
        String foodName = inputName.getText().toString();
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fix: Correctly get the selected category from AutoCompleteTextView
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

        String quantity = inputQuantity.getText().toString();
        if (quantity.isEmpty()) {
            Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // allow empty for expiry, availability
        String expiry = inputExpiry.getText().toString();

        Instant availableFrom = parseAvailabilityInstant(inputAvailableFrom);
        Instant availableTo = parseAvailabilityInstant(inputAvailableTo);

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

        boolean isPickup = rdPickup.isChecked();
        boolean isDelivery = rdDelivery.isChecked();

        if (!isPickup && !isDelivery) {
            Toast.makeText(this, "Please select Pick-Up or Delivery", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageKey = null;
        if (photoUri != null) {
            ImageServer imageServer = new ImageServer(this);
            imageKey = imageServer.saveImage(photoUri);
        }

        int currentUserId = new AuthHelper(this).getCurrentUser().getId();

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            // Fix: Use category.name() and fix undefined 'categoryName' variable
            boolean success = dbHelper.saveFoodItem(currentUserId, foodName, category.name(), quantity,
                    expiry, availableFrom, availableTo, isFree, cents, isPickup,
                    isDelivery, imageKey);
            if (success) {
                Toast.makeText(this, "Food Item Created", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(NewFoodItemActivity.this, DonorHomeActivity.class);
                startActivity(intent);
            } else {
                throw new IOException("DB save failed");
            }
        } catch (Exception e) {
            Log.d("NewFoodItemActivity.handleCreate", e.toString());
            Toast.makeText(this, "DB Save failed", Toast.LENGTH_LONG).show();
        }
    }

    private Instant parseAvailabilityInstant(EditText input) {
        String value = input.getText() == null ? "" : input.getText().toString().trim();
        if (value.isEmpty()) {
            return null;
        }

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(value, availabilityDisplayFormatter);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (Exception ignored) {
            Log.d("NewFoodItemActivity parsetime", "failed to parse time: " + value);
            return null;
        }
    }
}
