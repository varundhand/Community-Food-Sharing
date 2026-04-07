package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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

import database.DatabaseHelper;
import models.FoodItem;
import models.Request;
import models.RequestStatus;
import models.User;
import utils.ImageServer;

public class RecipientRequestActivity extends AppCompatActivity {
    public static final String EXTRA_REQ_ID = "request_id";

    TextView txtFoodItemName;
    ImageView imgFoodItem;
    TextView txtRequestStatus;
    ImageView imgDonor;
    TextView txtDonorName, txtDonorAddress, txtRequestDue;
    Button btnApprove, btnDecline, btnComplete;

    private DatabaseHelper dbHelper;
    private Request request;
    private User donor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Invalid Transition", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RecipientHomeActivity.class));
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        int requestId = extras.getInt(EXTRA_REQ_ID);
        try {
            request = dbHelper.getRequests(requestId, null, null, null, null, null, false).get(0);
        } catch (Exception e) {
            Toast.makeText(this, "Error Retrieving the record", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RecipientRequestActivity.this, RecipientHomeActivity.class));
            finish();
            return;
        }

        txtFoodItemName = findViewById(R.id.txtFoodItemName);
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtRequestStatus = findViewById(R.id.txtRequestStatus);
        imgDonor = findViewById(R.id.imgDonor);
        txtDonorName = findViewById(R.id.txtDonorName);
        txtDonorAddress = findViewById(R.id.txtDonorAddress);
        txtRequestDue = findViewById(R.id.txtRequestDue);
        btnApprove = findViewById(R.id.btnApprove);
        btnDecline = findViewById(R.id.btnDecline);
        btnComplete = findViewById(R.id.btnComplete);

        FoodItem foodItem = request.getFoodItem();

        txtFoodItemName.setText(foodItem.getName());
        setPhoto(foodItem.getImageKey(), imgFoodItem, true);
        txtRequestStatus.setText(request.getStatus().name());

        donor = dbHelper.getUser(foodItem.getDonorId());

        setPhoto(donor.getImageKey(), imgDonor, false);
        txtDonorName.setText("Name: " + donor.getName());
        txtDonorAddress.setText("Address: " + donor.getPostalAddress());

        if (foodItem.isPickupAvailable()) {
            txtRequestDue.setText("Pick Up due " + request.getFormattedDue());
        } else {
            // Pickup/Delivery is Mutually exclusive
            txtRequestDue.setText("Delivery Scheduled");
        }
    }

    private void setPhoto(String filename, ImageView imgView, boolean isFoodItem) {
        if (filename != null && !filename.isEmpty()) {
            ImageServer imgServer = new ImageServer(this);
            Bitmap bitmap = imgServer.loadImage(filename);
            if (bitmap != null) {
                imgView.setImageBitmap(bitmap);
                return;
            }
        }
        
        if (isFoodItem) {
            imgView.setImageResource(R.drawable.item_static);
        } else {
            Log.d("RecipientRequestActivity", "No donor photo selected");
        }
    }
}