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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

import adapters.DonorRequestListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.Request;
import models.RequestStatus;
import models.User;

public class DonorRequestListActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    AuthHelper authHelper;
    ArrayList<Request> allRequests;

    RecyclerView recyclerView;
    
    Button btnFilterAll, btnFilterUrgent, btnFilterNearby, btnFilterDairy;

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
        
        btnFilterAll = findViewById(R.id.filterAll);
        btnFilterUrgent = findViewById(R.id.filterUrgent);
        btnFilterNearby = findViewById(R.id.filterNearby);
        btnFilterDairy = findViewById(R.id.filterDairy);

        setupFilters();

        allRequests = dbHelper.getRequests(null, null, null, null, null, donor.getId(), false);
        allRequests = (ArrayList<Request>) allRequests.stream()
                // filter out COMPLETE and (PENDING but food is reserved, which means it is reserved for somebody else)
                .filter(r -> r.getStatus() != RequestStatus.COMPLETE && !(r.getStatus() == RequestStatus.PENDING && r.getFoodItem().isReserved()))
                .collect(Collectors.toList());
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

        btnFilterDairy.setOnClickListener(v -> {
            ArrayList<Request> dairy = (ArrayList<Request>) allRequests.stream()
                    .filter(r -> models.FoodCategory.DAIRY.name().equalsIgnoreCase(r.getFoodItem().getCategory()))
                    .collect(Collectors.toList());
            updateRecyclerView(dairy);
        });
    }

    private void updateRecyclerView(ArrayList<Request> requests) {
        recyclerView.setAdapter(new DonorRequestListRecyclerViewAdapter(requests));
    }
}