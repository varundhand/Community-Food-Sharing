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
import utils.ImageServer;

public class RecipientFoodItemActivity extends AppCompatActivity {
    FoodItem item;
    ImageView imgFoodItem;
    TextView txtFoodName;

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
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            item = dbHelper.getFoodItem(foodItemId);
        }

        if (item == null) {
            // Go back to donor home if the foodId is invalid
            Toast.makeText(this, "Invalid Food", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RecipientFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            return;
        }

        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtFoodName = findViewById(R.id.txtFoodName);
        String imgFilename = item.getImageKey();

        if (imgFilename != null && !imgFilename.isEmpty()) {
            setPreviewPhoto(imgFilename);
        }

        txtFoodName.setText(item.getName());
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