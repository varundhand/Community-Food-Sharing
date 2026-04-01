package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import database.DatabaseHelper;
import models.FoodItem;
import models.User;
import utils.ImageServer;

public class RecipientFoodItemActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    FoodItem item;
    User donor;
    // Food
    ImageView imgFoodItem;
    TextView txtFoodName, txtFoodCategory, txtFoodQuantity, txtFoodExpiry, txtFoodAvailableFrom,
            txtFoodAvailableTo, txtFoodAvailableType;
    // Donor
    ImageView imgDonor;
    TextView txtDonorName, txtDonorPhone, txtDonorPostalAddress, txtDonorPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_food_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int foodItemId = extras.getInt("FoodItemId");
            dbHelper = new DatabaseHelper(this);
            item = dbHelper.getFoodItem(foodItemId);
        }

        if (item == null) {
            // Go back to donor home if the foodId is invalid
            Toast.makeText(this, "Invalid Food", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RecipientFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            return;
        }

        donor = dbHelper.getUser(item.getDonorId());

        // FoodItem
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodCategory = findViewById(R.id.txtFoodCategory);
        txtFoodQuantity = findViewById(R.id.txtFoodQuantity);
        txtFoodExpiry = findViewById(R.id.txtFoodExpiry);
        txtFoodAvailableFrom = findViewById(R.id.txtFoodAvailableFrom);
        txtFoodAvailableTo = findViewById(R.id.txtFoodAvailableTo);
        txtFoodAvailableType = findViewById(R.id.txtFoodAvailableType);

        // Set values to Food Item
        String foodItemImageFile = item.getImageKey();

        if (foodItemImageFile != null && !foodItemImageFile.isEmpty()) {
            setPhoto(foodItemImageFile, imgFoodItem);
        }

        txtFoodName.setText(item.getName());
        txtFoodCategory.setText(item.getFoodCategory().toString());
        txtFoodQuantity.setText(item.getQuantity());
        txtFoodExpiry.setText(item.getExpiry());

        txtFoodAvailableFrom.setText(item.getFormattedAvailableFrom());
        txtFoodAvailableTo.setText(item.getFormattedAvailableTo());

        // Issue #35. pick up and delivery options are exclusive each other
        txtFoodAvailableType.setText(item.isPickupAvailable() ? "Available by Pick-Up" : "Available by delivery");

        // donor
        imgDonor = findViewById(R.id.imgDonor);
        txtDonorName = findViewById(R.id.txtDonorName);
        txtDonorPhone = findViewById(R.id.txtDonorPhone);
        txtDonorPostalAddress = findViewById(R.id.txtDonorPostalAddress);
        txtDonorPostalCode = findViewById(R.id.txtDonorPostalCode);

        // donor set values
        String donorImageFile = donor.getImageKey();
        if (donorImageFile != null && !donorImageFile.isEmpty()) {
            setPhoto(donorImageFile, imgDonor);
        }
        txtDonorName.setText(donor.getName());
        txtDonorPhone.setText(donor.getPhone());
        txtDonorPostalAddress.setText(donor.getPostalAddress());
        txtDonorPostalCode.setText(donor.getPostalCode());
    }

    private void setPhoto(String filename, ImageView imgView) {
        if (filename != null) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            imgView.setImageBitmap(bitmap);
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }
}