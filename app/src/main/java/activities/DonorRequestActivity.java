package activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import database.DatabaseHelper;
import models.FoodItem;
import models.Request;
import models.User;
import utils.ImageServer;

public class DonorRequestActivity extends AppCompatActivity {
    public static final String EXTRA_REQ_ID = "request_id";

    TextView txtFoodItemName;
    ImageView imgFoodItem;
    TextView txtRequestStatus;
    ImageView imgRecipient;
    TextView txtRecipientName, txtRecipientAddress, txtRequestDue;

    private DatabaseHelper dbHelper;
    private Request request;

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

        txtFoodItemName = findViewById(R.id.txtFoodItemName);
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtRequestStatus = findViewById(R.id.txtRequestStatus);
        imgRecipient = findViewById(R.id.imgRecipient);
        txtRecipientName = findViewById(R.id.txtRecipientName);
        txtRecipientAddress = findViewById(R.id.txtRecipientAddress);
        txtRequestDue = findViewById(R.id.txtRequestDue);

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

    public void handleApprove(View view) {}
    public void handleDecline(View view) {}
}