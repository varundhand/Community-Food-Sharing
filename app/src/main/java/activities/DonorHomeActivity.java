package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import database.AuthHelper;
import models.User;
import utils.ImageServer;

public class DonorHomeActivity extends AppCompatActivity {
    AuthHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authHelper = new AuthHelper(this);

        TextView donorName = findViewById(R.id.txtDonorName);

        User user = authHelper.getCurrentUser();
        if (user != null) {
            donorName.setText(user.getName());
        }

    }

    public void handleViewRequests(View view) {
        Intent intent = new Intent(DonorHomeActivity.this, DonorRequestListActivity.class);
        startActivity(intent);
    }

    public void handleViewFoodItems(View view) {
        Intent intent = new Intent(DonorHomeActivity.this, DonorFoodItemListActivity.class);
        startActivity(intent);
    }

    public void handleNewFood(View view) {
        Intent intent = new Intent(DonorHomeActivity.this, NewFoodItemActivity.class);
        startActivity(intent);
    }

    public void handleEditProfile(View view) {
        Intent intent = new Intent(DonorHomeActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void handleLogout(View view) {
        AuthHelper helper = new AuthHelper(this);
        helper.logout();
        Intent intent = new Intent(DonorHomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}