package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import utils.ImageServer;

public class DonorReadOnlyFoodItemActivity extends AppCompatActivity {
    FoodItem item;

    ImageView imgFoodItem;
    TextView txtFoodName, txtFoodCategory, txtFoodQuantity, txtFoodExpiry, txtFoodAvailableFrom,
            txtFoodAvailableTo, txtFoodFreeDiscounted, txtFoodPrice, txtFoodPickUpDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_read_only_food_item);
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
            Intent intent = new Intent(DonorReadOnlyFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            return;
        }

        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodCategory = findViewById(R.id.txtFoodCategory);
        txtFoodQuantity = findViewById(R.id.txtFoodQuantity);
        txtFoodExpiry = findViewById(R.id.txtFoodExpiry);
        txtFoodAvailableFrom = findViewById(R.id.txtFoodAvailableFrom);
        txtFoodAvailableTo = findViewById(R.id.txtFoodAvailableTo);
        txtFoodFreeDiscounted = findViewById(R.id.chipPriceType);
        txtFoodPrice = findViewById(R.id.txtFoodPrice);
        txtFoodPickUpDelivery = findViewById(R.id.chipOrderType);

        String imageKey = item.getImageKey();
        if (imageKey != null && !imageKey.isEmpty()) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(imageKey);
            imgFoodItem.setImageBitmap(bitmap);
        }

        txtFoodName.setText(item.getName());
        txtFoodCategory.setText(item.getCategory());
        txtFoodQuantity.setText(item.getQuantity());
        txtFoodExpiry.setText(item.getExpiry());
        txtFoodAvailableFrom.setText(item.getFormattedAvailableFrom());
        txtFoodAvailableTo.setText(item.getFormattedAvailableTo());
        if (item.isFree()) {
            txtFoodFreeDiscounted.setText("Offered free");
        } else {
            txtFoodFreeDiscounted.setText("Offered at discounted price");
        }
        txtFoodPrice.setText(String.format("$%.2f", item.getPriceDollar()));
        if (item.isPickupAvailable()) {
            // pickup and delivery are mutually exclusive
            txtFoodPickUpDelivery.setText("Available for pickup");
        } else {
            txtFoodPickUpDelivery.setText("Available for pickup");
        }

    }
}
