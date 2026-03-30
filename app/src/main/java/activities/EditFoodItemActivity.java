package activities;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.math.BigDecimal;

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
            // Debug
            Toast.makeText(this, "food id: " + foodItemId, Toast.LENGTH_SHORT).show();

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
}