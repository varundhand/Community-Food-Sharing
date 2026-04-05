package activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import adapters.FoodItemListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodItem;
import models.User;

public class DonorFoodItemListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    RecyclerView recyclerView;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_food_item_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        bottomNav = findViewById(R.id.bottomNav);

        User user = new AuthHelper(this).getCurrentUser();

        ArrayList<FoodItem> foodItems = dbHelper.listFoodItem(user.getId(), true);
        FoodItemListRecyclerViewAdapter adapter = new FoodItemListRecyclerViewAdapter(foodItems, EditFoodItemActivity.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_donations);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DonorHomeActivity.class));
                return true;
            } else if (id == R.id.nav_requests) {
                startActivity(new Intent(this, DonorRequestListActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}