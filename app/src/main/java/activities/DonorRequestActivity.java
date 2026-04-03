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
            request = dbHelper.getRequests(requestId, null, null, null, null, null).get(0);
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
        txtRecipientName.setText("Name: " + recipient.getName());
        txtRecipientAddress.setText("Address: " + recipient.getPostalAddress());

        if (foodItem.isPickupAvailable()) {
            txtRequestDue.setText("Pick Up due " + request.getFormattedDue());
        } else {
            // Pickup/Delivery is Mutually exclusive
            txtRequestDue.setText("Delivery");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            hideButton(btnApprove);
            hideButton(btnDecline);
        }

        if (request.getStatus() != RequestStatus.APPROVED) {
            // complete button and remind button is enabled only when the status is "Approved"
            hideButton(btnComplete);
            hideButton(btnRemind);
        }

        if (reminders != null && !reminders.isEmpty()) {
            // Hide remind button if reminder is already sent
            hideButton(btnRemind);
        }
    }

    private void hideButton(Button btn) {
        // Reference: https://stackoverflow.com/a/5756190
        btn.setEnabled(false);
        btn.setVisibility(View.GONE);
    }

    private void showButton(Button btn) {
        btn.setEnabled(true);
        btn.setVisibility(View.VISIBLE);
    }

    private void setPhoto(String filename, ImageView imgView) {
        if (filename != null) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            imgView.setImageBitmap(bitmap);
        } else {
            Log.d("DonorRequestActivity.setPhoto", "No media selected");
        }
    }

    public void handleApprove(View view) {
        dbHelper.updateRequestStatus(request.getId(), RequestStatus.APPROVED);
        txtRequestStatus.setText(RequestStatus.APPROVED.name());
        Toast.makeText(this, "The request is approved", Toast.LENGTH_SHORT);

        hideButton(btnApprove);
        hideButton(btnDecline);
        showButton(btnComplete);
        showButton(btnRemind);
    }

    public void handleDecline(View view) {
        dbHelper.updateRequestStatus(request.getId(), RequestStatus.DECLINED);
        txtRequestStatus.setText(RequestStatus.DECLINED.name());
        Toast.makeText(this, "The request is declined", Toast.LENGTH_SHORT);

        hideButton(btnApprove);
        hideButton(btnDecline);
        hideButton(btnComplete);
        hideButton(btnRemind);
    }

    public void handleRemind(View view) {
        String content;
        if (request.getFoodItem().isPickupAvailable()) {
            // PickUp and Delivery are mutually exclusive
            content = "Please pick up the item by " + request.getFormattedDue();
        } else {
            content = "The food will be delivered soon";
        }
        dbHelper.createReminder(request.getRecipient().getId(), request.getId(), "Reminder", content, null);
        Toast.makeText(this, "Reminded the recipient", Toast.LENGTH_LONG);

        hideButton(btnRemind);
    }

    public void handleComplete(View view) {
        dbHelper.updateRequestStatus(request.getId(), RequestStatus.COMPLETE);
        dbHelper.completeFoodItem(request.getFoodItem().getId(), Instant.now());
        txtRequestStatus.setText(RequestStatus.COMPLETE.name());
        Toast.makeText(this, "The request is complete", Toast.LENGTH_SHORT);

        hideButton(btnApprove);
        hideButton(btnDecline);
        hideButton(btnComplete);
        hideButton(btnRemind);
    }
}