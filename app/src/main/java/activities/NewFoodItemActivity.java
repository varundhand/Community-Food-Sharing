package activities;

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

import models.FoodCategory;
import utils.ImageServer;

public class NewFoodItemActivity extends AppCompatActivity {
    EditText inputName, inputQuantity, inputExpiry, inputAvailableFrom, inputAvailableTo, inputPrice;
    RadioButton rdFree, rdDiscounted;
    CheckBox chkPickUp, chkDelivery;
    Spinner spinnerCategory;
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

        chkPickUp = findViewById(R.id.chkPickup);
        chkDelivery = findViewById(R.id.chkDelivery);

        spinnerCategory = findViewById(R.id.spinnerFoodCategory);
        imgUploadPreview = findViewById(R.id.imgUploadPreview);

        // spinner from enum
        // reference: https://stackoverflow.com/a/8619228
        spinnerCategory.setAdapter(new ArrayAdapter<FoodCategory>(this, android.R.layout.simple_spinner_item, FoodCategory.values()));

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        photoUri = uri;
                        ImageServer imgServer = new ImageServer(this);
                        Bitmap bitmap = imgServer.loadImage(uri);
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

        // Let's assume Expiry, availability can be null (N/A, or anytime)

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
                Toast.makeText(this, "Please enter valid price" + cents, Toast.LENGTH_SHORT).show();
            }
        }

        boolean allowPickup = chkPickUp.isChecked();
        boolean allowDelivery = chkDelivery.isChecked();

        Toast.makeText(this, "reached end", Toast.LENGTH_SHORT).show();

    }
}