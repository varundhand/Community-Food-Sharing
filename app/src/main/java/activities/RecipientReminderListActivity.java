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

import adapters.RecipientReminderListRecyclerViewAdapter;
import database.AuthHelper;
import database.DatabaseHelper;
import models.Reminder;
import models.User;

public class RecipientReminderListActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    AuthHelper authHelper;
    RecyclerView recyclerView;

    ArrayList<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_reminder_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = new DatabaseHelper(this);
        authHelper = new AuthHelper(this);
        recyclerView = findViewById(R.id.recyclerView);

        User user = authHelper.getCurrentUser();
        reminders = dbHelper.getReminders(user.getId(), null, false);
        RecipientReminderListRecyclerViewAdapter adapter = new RecipientReminderListRecyclerViewAdapter(reminders);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}