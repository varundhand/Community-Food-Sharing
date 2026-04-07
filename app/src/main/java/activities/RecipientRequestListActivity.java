package activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;
import java.util.stream.Collectors;

import adapters.RecipientRequestListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodCategory;
import models.Request;
import models.RequestStatus;
import models.User;

public class RecipientRequestListActivity extends AppCompatActivity {
    private final String SPINNER_VALUE_ALL = "ALL";

    DatabaseHelper dbHelper;
    AuthHelper authHelper;

    Spinner spinnerStatus;
    RecyclerView recyclerView;
    ArrayList<Request> requests;

    ArrayList<String> spinnerValues;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_request_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);
        authHelper = new AuthHelper(this);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        recyclerView = findViewById(R.id.recyclerView);

        user = authHelper.getCurrentUser();

        // spinner from enum
        spinnerValues = new ArrayList<>();
        spinnerValues.add(SPINNER_VALUE_ALL);
        for (RequestStatus status : RequestStatus.values()) {
            spinnerValues.add(status.name());
        }
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
        spinnerStatus.setAdapter(spinnerAdapter);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos == 0) {
                    recyclerView.swapAdapter(selectAll(), true); // id is not stable
                    return;
                }
                String statusStr = spinnerValues.get(pos);
                recyclerView.swapAdapter(selectByStatus(RequestStatus.valueOf(statusStr)), true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        recyclerView.setAdapter(selectAll());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private RecipientRequestListRecyclerViewAdapter selectAll() {
        requests = dbHelper.getRequests(null, null, user.getId(), null, null, null, false);
        requests = (ArrayList<Request>) requests.stream()
                // filter out PENDING but food is reserved, which means it is reserved for somebody else
                .filter(r -> !(r.getStatus() == RequestStatus.PENDING && r.getFoodItem().isReserved()))
                .collect(Collectors.toList());
        return new RecipientRequestListRecyclerViewAdapter(requests);
    }

    private RecipientRequestListRecyclerViewAdapter selectByStatus(RequestStatus status) {
        requests = dbHelper.getRequests(null, null, user.getId(), null, status, null, false);
        requests = (ArrayList<Request>) requests.stream()
                // filter out PENDING but food is reserved, which means it is reserved for somebody else
                .filter(r -> !(r.getStatus() == RequestStatus.PENDING && r.getFoodItem().isReserved()))
                .collect(Collectors.toList());
        return new RecipientRequestListRecyclerViewAdapter(requests);
    }

}