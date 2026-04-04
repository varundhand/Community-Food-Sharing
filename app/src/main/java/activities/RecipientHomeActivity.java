package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.util.ArrayList;

import database.AuthHelper;
import database.DatabaseHelper;
import models.Reminder;
import models.User;

public class RecipientHomeActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ArrayList<Reminder> unreadReminders;

    TextView txtRecipientName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        txtRecipientName = findViewById(R.id.txtRecipientName);

        AuthHelper authHelper = new AuthHelper(this);
        User user = authHelper.getCurrentUser();

        if (user == null) {
            authHelper.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        txtRecipientName.setText(user.getName());

        unreadReminders = dbHelper.getReminders(user.getId(), null, true);

        if (!unreadReminders.isEmpty()) {
            String message;
            if (unreadReminders.size() == 1) {
                message = "There is an unread reminder";
            } else {
                message = String.format("There are %d unread reminders", unreadReminders.size());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setMessage(message);
            builder.show();
        }
    }

    public void handleViewReminders(View view) {
        Intent intent = new Intent(this, RecipientReminderListActivity.class);
        startActivity(intent);
    }

    public void handleViewRequests(View view) {
        Intent intent = new Intent(this, RecipientRequestListActivity.class);
        startActivity(intent);
    }

    public void handleViewFoodItemsSearch(View view) {
        Intent intent = new Intent(RecipientHomeActivity.this, RecipientFoodItemSearchActivity.class);
        startActivity(intent);
    }

    public void handleViewNearbyFood(View view) {
        Intent intent = new Intent(RecipientHomeActivity.this, RecipientNearbyFoodItemActivity.class);
        startActivity(intent);
    }

    public void handleEditProfile(View view) {
        Intent intent = new Intent(RecipientHomeActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void handleViewRequestHistory(View view) {
        Intent intent = new Intent(RecipientHomeActivity.this, RecipientRequestHistoryActivity.class);
        startActivity(intent);
    }

    public void handleLogout(View view) {
        AuthHelper helper = new AuthHelper(this);
        helper.logout();
        Intent intent = new Intent(RecipientHomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}