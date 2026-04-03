package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import database.AuthHelper;

public class RecipientHomeActivity extends AppCompatActivity {

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
    }

    public void handleViewRequests(View view) {
        Intent intent = new Intent(this, RecipientRequestListActivity.class);
        startActivity(intent);
    }

    public void handleViewNearbyFood(View view) {
        Intent intent = new Intent(RecipientHomeActivity.this, RecipientNearbyFoodItemActivity.class);
        startActivity(intent);
    }

    public void handleLogout(View view) {
        AuthHelper helper = new AuthHelper(this);
        helper.logout();
        Intent intent = new Intent(RecipientHomeActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}