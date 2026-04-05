package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import database.AuthHelper;
import database.DatabaseHelper;
import models.Request;
import models.RequestStatus;
import models.User;

public class DonorHomeActivity extends AppCompatActivity {
    AuthHelper authHelper;
    DatabaseHelper dbHelper;
    ArrayList<Request> pendingRequests;
    BottomNavigationView bottomNav;

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
        dbHelper = new DatabaseHelper(this);

        TextView donorName = findViewById(R.id.txtDonorName);
        bottomNav = findViewById(R.id.bottomNav);

        User user = authHelper.getCurrentUser();

        if (user == null) {
            authHelper.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        donorName.setText(user.getName());

        pendingRequests = dbHelper.getRequests(null, null, null, null, RequestStatus.PENDING, user.getId());

        if (!pendingRequests.isEmpty()) {
            String message;
            if (pendingRequests.size() == 1) {
                message = "There is a pending request";
            } else {
                message = String.format("There are %d pending requests", pendingRequests.size());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage(message);
            builder.show();
        }

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_donations) {
                startActivity(new Intent(this, DonorFoodItemListActivity.class));
                return true;
            } else if (id == R.id.nav_requests) {
                startActivity(new Intent(this, DonorRequestListActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            }
            return id == R.id.nav_home;
        });
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

    public void handleFoodItemHistory(View view) {
        Intent intent = new Intent(DonorHomeActivity.this, DonorFoodItemHistoryActivity.class);
        startActivity(intent);
    }

    public void handleLogout(View view) {
        AuthHelper helper = new AuthHelper(this);
        helper.logout();
        Intent intent = new Intent(DonorHomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}