package activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;

import adapters.DonorRequestListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.Request;
import models.RequestStatus;
import models.User;

public class DonorRequestListActivity extends AppCompatActivity {
    private final String SPINNER_VALUE_ALL = "ALL";

    DatabaseHelper dbHelper;
    AuthHelper authHelper;
    ArrayList<Request> requests;

    AutoCompleteTextView spinnerStatus;
    RecyclerView recyclerView;

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

        spinnerStatus = findViewById(R.id.spinnerStatus);
        recyclerView = findViewById(R.id.recyclerView);

        ArrayList<String> spinnerValues;

        donor = authHelper.getCurrentUser();

        // spinner from enum
        spinnerValues = new ArrayList<>();
        spinnerValues.add(SPINNER_VALUE_ALL);
        for (RequestStatus status : RequestStatus.values()) {
            spinnerValues.add(status.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, spinnerValues);
        spinnerStatus.setAdapter(adapter);
        
        // Default value
        spinnerStatus.setText(SPINNER_VALUE_ALL, false);

        spinnerStatus.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                recyclerView.swapAdapter(selectAll(), true);
                return;
            }
            String statusStr = spinnerValues.get(position);
            recyclerView.swapAdapter(selectByStatus(RequestStatus.valueOf(statusStr)), true);
        });

        recyclerView.setAdapter(selectAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private DonorRequestListRecyclerViewAdapter selectAll() {
        requests = dbHelper.getRequests(null, null, null,null, null, donor.getId());
        return new DonorRequestListRecyclerViewAdapter(requests);
    }

    private DonorRequestListRecyclerViewAdapter selectByStatus(RequestStatus status) {
        requests = dbHelper.getRequests(null, null, null, null, status, donor.getId());
        return new DonorRequestListRecyclerViewAdapter(requests);
    }
}