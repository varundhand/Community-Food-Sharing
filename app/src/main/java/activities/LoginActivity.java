package activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import database.AuthHelper;
import models.User;
import models.UserType;

public class LoginActivity extends AppCompatActivity {
    EditText inputEmail, inputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
    }

    public void handleLogin(View view) {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        AuthHelper authHelper = new AuthHelper(this);
        User user = authHelper.login(email, password);

        if (user == null) {
            Toast.makeText(this, R.string.login_toast_login_failed, Toast.LENGTH_LONG).show();
        }

        if (user.getUserType() == UserType.DONOR) {
            // TODO: Navigate to DONOR HOME
            Toast.makeText(this, "logged in as a donor", Toast.LENGTH_LONG).show();
        }
        else if (user.getUserType() == UserType.RECIPIENT) {
            // TODO: Navigate to RECIPIENT HOME
            Toast.makeText(this, "logged in as a recipient", Toast.LENGTH_LONG).show();
        }
        else {}
    }
}