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

import java.time.format.DateTimeFormatter;

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
    TextView txtDonorName;

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

        // donor
        txtDonorName = findViewById(R.id.txtDonorName);

        String imgFilename = item.getImageKey();

        if (imgFilename != null && !imgFilename.isEmpty()) {
            setPreviewPhoto(imgFilename);
        }


        txtFoodName.setText(item.getName());
        txtFoodCategory.setText(item.getFoodCategory().toString());
        txtFoodQuantity.setText(item.getQuantity());
        txtFoodExpiry.setText(item.getExpiry());

        txtFoodAvailableFrom.setText(item.getFormattedAvailableFrom());
        txtFoodAvailableTo.setText(item.getFormattedAvailableTo());

        // Issue #35. pick up and delivery options are exclusive each other
        txtFoodAvailableType.setText(item.isPickupAvailable() ? "Available by Pick-Up" : "Available by delivery");

        txtDonorName.setText(donor.getName());
    }

    private void setPreviewPhoto(String filename) {
        if (filename != null) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            imgFoodItem.setImageBitmap(bitmap);
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }
}