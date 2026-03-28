package activities;

import android.os.Bundle;

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
import models.FoodItem;
import models.User;

public class DonorFoodItemListActivity extends AppCompatActivity {

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

        User user = new AuthHelper(this).getCurrentUser();

        try (DatabaseHelper dbHelper = new DatabaseHelper(this)) {
            ArrayList<FoodItem> foodItems = dbHelper.listFoodItem(user.getId());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your Food Items");
            StringBuilder message = new StringBuilder();
            for (FoodItem item : foodItems) {
                message.append(item.getName()).append(" - ").append(item.getQuantity()).append("\n");
            }
            builder.setMessage(message.toString());
            builder.setPositiveButton("OK", null);
            builder.show();
        };
    }
}