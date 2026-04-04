package activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

import adapters.RecipientFoodItemListRecyclerViewAdapter;
import database.DatabaseHelper;
import models.FoodItem;

public class RecipientFoodItemSearchActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    ArrayList<FoodItem> foodItems;
    EditText inputSearch;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_food_item_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        inputSearch = findViewById(R.id.inputSearch);

        foodItems = dbHelper.searchFoodItemsByName(null, true);
        RecipientFoodItemListRecyclerViewAdapter adapter = new RecipientFoodItemListRecyclerViewAdapter(foodItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int act, KeyEvent keyEvent) {
                if (act == EditorInfo.IME_ACTION_SEARCH) {
                    // Reference https://stackoverflow.com/a/26645164

                    foodItems = dbHelper.searchFoodItemsByName(inputSearch.getText().toString(), true);
                    RecipientFoodItemListRecyclerViewAdapter adapter = new RecipientFoodItemListRecyclerViewAdapter(foodItems);
                    recyclerView.swapAdapter(adapter, false);
                }
                return false;
            }
        });

    }
}