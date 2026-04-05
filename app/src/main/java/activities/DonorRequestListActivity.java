package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

import adapters.DonorRequestListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.Request;
import models.User;

public class DonorRequestListActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    AuthHelper authHelper;
    ArrayList<Request> allRequests;

    RecyclerView recyclerView;
    BottomNavigationView bottomNav;
    
    Button btnFilterAll, btnFilterUrgent, btnFilterNearby, btnFilterGroceries;

    User donor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_request_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        authHelper = new AuthHelper(this);
        donor = authHelper.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        bottomNav = findViewById(R.id.bottomNav);
        
        btnFilterAll = findViewById(R.id.filterAll);
        btnFilterUrgent = findViewById(R.id.filterUrgent);
        btnFilterNearby = findViewById(R.id.filterNearby);
        btnFilterGroceries = findViewById(R.id.filterGroceries);

        setupFilters();
        setupNavigation();

        allRequests = dbHelper.getRequests(null, null, null, null, null, donor.getId());
        updateRecyclerView(allRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFilters() {
        btnFilterAll.setOnClickListener(v -> updateRecyclerView(allRequests));

        btnFilterUrgent.setOnClickListener(v -> {
            Instant tomorrow = Instant.now().plus(1, ChronoUnit.DAYS);
            ArrayList<Request> urgent = (ArrayList<Request>) allRequests.stream()
                    .filter(r -> r.getDue() != null && r.getDue().toInstant().isBefore(tomorrow))
                    .collect(Collectors.toList());
            updateRecyclerView(urgent);
        });

        btnFilterNearby.setOnClickListener(v -> {
            String donorPrefix = donor.getPostalCode().substring(0, 3);
            ArrayList<Request> nearby = (ArrayList<Request>) allRequests.stream()
                    .filter(r -> r.getRecipient().getPostalCode().startsWith(donorPrefix))
                    .collect(Collectors.toList());
            updateRecyclerView(nearby);
        });

        btnFilterGroceries.setOnClickListener(v -> {
            ArrayList<Request> groceries = (ArrayList<Request>) allRequests.stream()
                    .filter(r -> "Groceries".equalsIgnoreCase(r.getFoodItem().getCategory()))
                    .collect(Collectors.toList());
            updateRecyclerView(groceries);
        });
    }

    private void updateRecyclerView(ArrayList<Request> requests) {
        recyclerView.setAdapter(new DonorRequestListRecyclerViewAdapter(requests));
    }

    private void setupNavigation() {
        bottomNav.setSelectedItemId(R.id.nav_requests);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DonorHomeActivity.class));
                return true;
            } else if (id == R.id.nav_donations) {
                startActivity(new Intent(this, DonorFoodItemListActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            }
            return false;
        });
    }
}