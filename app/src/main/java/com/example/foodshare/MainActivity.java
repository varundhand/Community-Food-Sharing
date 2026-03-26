package com.example.foodshare;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import activities.DonorHomeActivity;
import activities.RecipientHomeActivity;
import activities.WelcomeActivity;
import database.AuthHelper;
import models.User;
import models.UserSession;
import models.UserType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AuthHelper authHelper = new AuthHelper(this);
        User user = authHelper.getCurrentUser();
        if (user == null) {
            // Go welcome screen if the user is not logged in
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        } else if (user.getUserType() == UserType.DONOR) {
            Intent intent = new Intent(MainActivity.this, DonorHomeActivity.class);
            startActivity(intent);
        } else if (user.getUserType() == UserType.RECIPIENT) {
            Intent intent = new Intent(MainActivity.this, RecipientHomeActivity.class);
            startActivity(intent);
        } else {
            // invalid. logout and navigate to Welcome Screen
            authHelper.logout();
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
        }

    }
}