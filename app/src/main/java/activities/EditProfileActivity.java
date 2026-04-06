package activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import database.AuthHelper;
import database.DatabaseHelper;
import models.User;
import models.UserType;
import utils.ImageServer;

public class EditProfileActivity extends AppCompatActivity {
    AuthHelper authHelper;
    DatabaseHelper dbHelper;

    TextView txtUserType;
    EditText inputName;
    TextView txtUserEmail;
    EditText inputPhone, inputPostalCode, inputPostalAddress;


    ImageView imgUploadPreview;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authHelper = new AuthHelper(this);
        dbHelper = new DatabaseHelper(this);

        user = authHelper.getCurrentUser();

        txtUserType = findViewById(R.id.txtUserType);
        inputName = findViewById(R.id.inputName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        inputPhone = findViewById(R.id.inputPhone);
        inputPostalCode = findViewById(R.id.inputPostalCode);
        inputPostalAddress = findViewById(R.id.inputPostalAddress);
        imgUploadPreview = findViewById(R.id.imgUploadPreview);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::setPreviewPhotoCallback);
        setValues();
    }

    private void setPreviewPhotoCallback(Uri uri) {
        if (uri == null) return;

        // set the uri to the preview then show dialog
        setPreviewPhoto(uri);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changing Photo. Confirm?");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String imageKey = updatePhoto(uri);

                if (imageKey != null) {
                    // set updated image
                    setPreviewPhoto(imageKey);
                    user.setImageKey(imageKey);
                    Toast.makeText(EditProfileActivity.this, "Changed the Photo", Toast.LENGTH_LONG).show();
                } else {
                    // set previous image
                    setPreviewPhoto(user.getImageKey());
                    Toast.makeText(EditProfileActivity.this, "Failed to Change the Photo", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.show();
    }

    private void setPreviewPhoto(Uri uri) {
        if (uri != null) {
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

            // clear image view
            // reference: https://stackoverflow.com/a/8243184
            imgUploadPreview.setImageResource(android.R.drawable.sym_def_app_icon);
            return;
        }

        // valid image
        imgUploadPreview.setImageBitmap(bitmap);
    }

    private void setValues() {
        if (user == null) return;

        inputName.setText(user.getName());
        txtUserType.setText(user.getUserType().name());
        txtUserEmail.setText(user.getEmail());
        inputPhone.setText(user.getPhone());
        inputPostalCode.setText(user.getPostalCode());
        inputPostalAddress.setText(user.getPostalAddress());

        setPreviewPhoto(user.getImageKey());
    }

    public void pickPhoto(View view) {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private String updatePhoto(Uri uri) {
        ImageServer imageServer = new ImageServer(this);
        String imageKey = imageServer.saveImage(uri);
        boolean success = dbHelper.updateUser(user.getId(), null, null, null, null, imageKey);
        if (success) return imageKey;
        else return null;
    }

    public void handleRemovePhoto(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Removing Profile Photo");
        builder.setMessage("Please confirm");
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // first, remove it from db
                // then remove it from filesystem
                String imgKey = user.getImageKey();
                boolean success = dbHelper.removePhotoFromUser(user.getId());

                if (success) {
                    // show toast regardless of the result of removal from file system (because it is already not referenced)
                    Toast.makeText(EditProfileActivity.this, "Successfully removed photo", Toast.LENGTH_LONG).show();
                    new ImageServer(EditProfileActivity.this).removeImage(imgKey);
                    imgUploadPreview.setImageResource(android.R.drawable.sym_def_app_icon);
                }
                else {
                    Toast.makeText(EditProfileActivity.this, "DB Save failed", Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.show();
    }

    public void handleSaveUser(View view) {
        String name = inputName.getText().toString();
        String phone = inputPhone.getText().toString();
        String postalCode = inputPostalCode.getText().toString();
        String postalAddress = inputPostalAddress.getText().toString();

        boolean success = dbHelper.updateUser(user.getId(), name, phone, postalCode, postalAddress, null);
        if (success) {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_LONG).show();
            Intent intent;
            if (user.getUserType() == UserType.DONOR) {
                intent = new Intent(EditProfileActivity.this, DonorHomeActivity.class);
            } else if (user.getUserType() == UserType.RECIPIENT) {
                intent = new Intent(EditProfileActivity.this, RecipientHomeActivity.class);
            } else {
                // unexpected user. logout the user and and navigate to login
                authHelper.logout();
                intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            }
            startActivity(intent);
        } else {
            Toast.makeText(this, "DB Save failed", Toast.LENGTH_LONG).show();
        }
    }
}