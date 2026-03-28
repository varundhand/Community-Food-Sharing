package activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import utils.ImageServer;

public class NewFoodActivity extends AppCompatActivity {
    ImageView imgUploadCandidate;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imgUploadCandidate = findViewById(R.id.imgUploadPreview);

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
                            imgUploadCandidate.setImageResource(0);
                            return;
                        }

                        // valid image
                        imgUploadCandidate.setImageBitmap(bitmap);
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
}