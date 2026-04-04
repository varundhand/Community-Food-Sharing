package activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;

import adapters.FoodItemListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodItem;
import models.User;

public class DonorFoodItemListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    ArrayList<FoodItem> foodItems;
    RecyclerView recyclerView;

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

        User user = new AuthHelper(this).getCurrentUser();

        foodItems = dbHelper.listFoodItem(user.getId(), true);
        FoodItemListRecyclerViewAdapter adapter = new FoodItemListRecyclerViewAdapter(foodItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}