package activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;

import adapters.FoodItemListRecyclerViewAdapter;
import adapters.RecipientFoodItemListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodItem;
import models.User;

public class RecipientNearbyFoodItemActivity extends AppCompatActivity {
    TextView txtPostalCode;
    ArrayList<FoodItem> foodItems;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_nearby_food_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtPostalCode = findViewById(R.id.txtPostalCode);
        recyclerView = findViewById(R.id.recyclerView);

        User user = new AuthHelper(this).getCurrentUser();

        txtPostalCode.setText(user.getPostalCode());

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            foodItems = dbHelper.listNearbyFoodItems(user.getPostalCode());
            RecipientFoodItemListRecyclerViewAdapter adapter = new RecipientFoodItemListRecyclerViewAdapter(foodItems);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        };
    }
}