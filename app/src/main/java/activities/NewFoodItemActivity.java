package activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import models.FoodCategory;
import utils.ImageServer;

public class NewFoodItemActivity extends AppCompatActivity {
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
        // for debug for now
        FoodCategory category = (FoodCategory) spinnerCategory.getSelectedItem();
        Toast.makeText(this, "Selected Category: " + category.name(), Toast.LENGTH_SHORT).show();
    }
}