package activities;

import android.os.Bundle;

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

public class DonorRequestListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    AuthHelper authHelper;
    ArrayList<Request> requests;

    RecyclerView recyclerView;

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
        requests = dbHelper.getRequests(null, null, null, null, authHelper.getCurrentUser().getId());

        recyclerView = findViewById(R.id.recyclerView);
        DonorRequestListRecyclerViewAdapter adapter = new DonorRequestListRecyclerViewAdapter(requests);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}