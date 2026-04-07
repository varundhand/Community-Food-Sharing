package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.time.Instant;
import java.util.ArrayList;

import database.DatabaseHelper;
import models.FoodItem;
import models.Reminder;
import models.Request;
import models.RequestStatus;
import models.User;
import utils.ImageServer;

public class DonorRequestActivity extends AppCompatActivity {
    public static final String EXTRA_REQ_ID = "request_id";

    TextView txtFoodItemName;
    ImageView imgFoodItem;
    TextView txtRequestStatus;
    ImageView imgRecipient;
    TextView txtRecipientName, txtRecipientAddress, txtRequestDue;
    Button btnApprove, btnDecline, btnComplete, btnRemind;

    private DatabaseHelper dbHelper;
    private Request request;
    private ArrayList<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donor_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Invalid Transition", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DonorRequestActivity.this, DonorHomeActivity.class));
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        int requestId = extras.getInt(EXTRA_REQ_ID);
        try {
            request = dbHelper.getRequests(requestId, null, null, null, null, null, false).get(0);
        } catch (Exception e) {
            Toast.makeText(this, "Error Retrieving the record", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DonorRequestActivity.this, DonorHomeActivity.class));
            finish();
            return;
        }

        reminders = dbHelper.getReminders(null, request.getId(), false);

        txtFoodItemName = findViewById(R.id.txtFoodItemName);
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtRequestStatus = findViewById(R.id.txtRequestStatus);
        imgRecipient = findViewById(R.id.imgRecipient);
        txtRecipientName = findViewById(R.id.txtRecipientName);
        txtRecipientAddress = findViewById(R.id.txtRecipientAddress);
        txtRequestDue = findViewById(R.id.txtRequestDue);
        btnApprove = findViewById(R.id.btnApprove);
        btnDecline = findViewById(R.id.btnDecline);
        btnComplete = findViewById(R.id.btnComplete);
        btnRemind = findViewById(R.id.btnRemind);

        FoodItem foodItem = request.getFoodItem();

        txtFoodItemName.setText(foodItem.getName());
        setPhoto(foodItem.getImageKey(), imgFoodItem);
        txtRequestStatus.setText(request.getStatus().name());

        User recipient = request.getRecipient();
        setPhoto(recipient.getImageKey(), imgRecipient);
        txtRecipientName.setText(recipient.getName());
        txtRecipientAddress.setText(recipient.getPostalAddress());

        if (foodItem.isPickupAvailable()) {
            txtRequestDue.setText("Pick Up due " + request.getFormattedDue());
        } else {
            txtRequestDue.setText("Delivery requested");
        }

        updateUIBasedOnStatus();
    }

    private void updateUIBasedOnStatus() {
        if (request.getStatus() != RequestStatus.PENDING) {
            hideButton(btnApprove);
            hideButton(btnDecline);
        } else {
            showButton(btnApprove);
            showButton(btnDecline);
        }

        if (request.getStatus() == RequestStatus.APPROVED) {
            showButton(btnComplete);
            if (reminders == null || reminders.isEmpty()) {
                showButton(btnRemind);
            } else {
                hideButton(btnRemind);
            }
        } else {
            hideButton(btnComplete);
            hideButton(btnRemind);
        }
    }

    private void hideButton(Button btn) {
        btn.setEnabled(false);
        btn.setVisibility(View.GONE);
    }

    private void showButton(Button btn) {
        btn.setEnabled(true);
        btn.setVisibility(View.VISIBLE);
    }

    private void setPhoto(String filename, ImageView imgView) {
        if (filename != null && !filename.isEmpty()) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            if (bitmap != null) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }

    public void handleApprove(View view) {
        if (request == null) return;

        // 1. Update Database
        dbHelper.updateRequestStatus(request.getId(), RequestStatus.APPROVED);
        dbHelper.reserveFoodItem(request.getFoodItemId(), Instant.now());

        // 2. Update Local Object - Use the correct setter from your Request model
        // If the method is named differently, change 'setRequestStatus' to that name
        request.setRequestStatus(RequestStatus.APPROVED);

        // 3. Update UI
        txtRequestStatus.setText(RequestStatus.APPROVED.name());
        Toast.makeText(this, "The request is approved", Toast.LENGTH_SHORT).show();
        updateUIBasedOnStatus();
    }

    public void handleDecline(View view) {
        if (request == null) return;

        dbHelper.updateRequestStatus(request.getId(), RequestStatus.DECLINED);
        // Update local object
        request.setRequestStatus(RequestStatus.DECLINED);

        txtRequestStatus.setText(RequestStatus.DECLINED.name());
        Toast.makeText(this, "The request is declined", Toast.LENGTH_SHORT).show();
        updateUIBasedOnStatus();
    }

    public void handleComplete(View view) {
        if (request == null) return;

        dbHelper.updateRequestStatus(request.getId(), RequestStatus.COMPLETE);
        dbHelper.completeFoodItem(request.getFoodItem().getId(), Instant.now());
        // Update local object
        request.setRequestStatus(RequestStatus.COMPLETE);

        txtRequestStatus.setText(RequestStatus.COMPLETE.name());
        Toast.makeText(this, "The request is complete", Toast.LENGTH_SHORT).show();
        updateUIBasedOnStatus();
    }
    public void handleRemind(View view) {
        String content;
        if (request.getFoodItem().isPickupAvailable()) {
            content = "Please pick up the item by " + request.getFormattedDue();
        } else {
            content = "The food will be delivered soon";
        }
        dbHelper.createReminder(request.getRecipient().getId(), request.getId(), "Reminder", content, null);
        Toast.makeText(this, "Reminded the recipient", Toast.LENGTH_LONG).show();
        hideButton(btnRemind);
    }}

