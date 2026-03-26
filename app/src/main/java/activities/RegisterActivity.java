package activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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

import database.AuthHelper;
import models.User;
import models.UserRegistrationForm;
import models.UserSession;
import models.UserType;
import utils.ImageServer;

public class RegisterActivity extends AppCompatActivity {
    RadioButton rdDonor, rdRecipient;
    EditText inputName, inputEmail, inputPassword, inputPhone, inputPostalCode, inputPostalAddress;
    TextView txtSelectedPhotoUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Uri photoUri;
//    Button btnUploadProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rdDonor = findViewById(R.id.rdDonor);
        rdRecipient = findViewById(R.id.rdRecipient);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPhone = findViewById(R.id.inputPhone);
        inputPostalCode = findViewById(R.id.inputPostalCode);
        inputPostalAddress = findViewById(R.id.inputPostalAddress);
        txtSelectedPhotoUri = findViewById(R.id.txtSelectedPhotoUri);

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        photoUri = uri;
                        txtSelectedPhotoUri.setText("Selected URI: " + uri);
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

    public void handleRegister(View view) {
        UserType userType;
        if (rdDonor.isChecked()) userType = UserType.DONOR;
        else if (rdRecipient.isChecked()) userType = UserType.RECIPIENT;
        else {
            Toast.makeText(this, R.string.register_toast_rd_not_selected, Toast.LENGTH_LONG).show();
            return;
        }

        // Save image to local storage if uri is not empty
        String imageKey = null;
        if (photoUri != null) {
            ImageServer imageServer = new ImageServer(this);
            imageKey = imageServer.saveImage(photoUri);
        }

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String phone = inputPhone.getText().toString();
        String postalCode = inputPostalCode.getText().toString();
        String postalAddress = inputPostalAddress.getText().toString();

        UserRegistrationForm form = new UserRegistrationForm(name, userType,
                email, password, phone, postalCode, postalAddress, imageKey);
        if (!form.isValid()) {
            Toast.makeText(this, R.string.register_toast_invalid_form, Toast.LENGTH_LONG).show();
            return;
        }

        AuthHelper authHelper = new AuthHelper(this);
        boolean registered = authHelper.registerUser(form);
        if (!registered) {
            Toast.makeText(this, R.string.register_toast_register_failed, Toast.LENGTH_LONG).show();
            return;
        }

        User user = authHelper.login(email, password);

        if (user.getUserType() == UserType.DONOR) {
            // TODO: Navigate to DONOR HOME
            Toast.makeText(this, "registered as a donor", Toast.LENGTH_LONG).show();
        }
        else if (user.getUserType() == UserType.RECIPIENT) {
            // TODO: Navigate to RECIPIENT HOME
            Toast.makeText(this, "registered as a recipient", Toast.LENGTH_LONG).show();
        }
        else {}

    }


}