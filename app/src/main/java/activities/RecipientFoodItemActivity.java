package activities;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodshare.R;

import java.time.Instant;
import java.util.ArrayList;

import database.AuthHelper;
import database.DatabaseHelper;
import models.FoodItem;
import models.Request;
import models.RequestStatus;
import models.User;
import utils.ImageServer;

public class RecipientFoodItemActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    AuthHelper authHelper;

    User currentUser;

    FoodItem item;
    User donor;
    ArrayList<Request> requests;

    // Food
    ImageView imgFoodItem;
    TextView txtFoodName, txtFoodCategory, txtFoodQuantity, txtFoodExpiry, txtFoodAvailableFrom,
            txtFoodAvailableTo, txtFoodAvailableType;

    // Requests
    Button btnRequest;
    // Donor
    ImageView imgDonor;
    TextView txtDonorName, txtDonorPhone, txtDonorPostalAddress, txtDonorPostalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipient_food_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int foodItemId = extras.getInt("FoodItemId");
            dbHelper = new DatabaseHelper(this);
            item = dbHelper.getFoodItem(foodItemId);
        }

        if (item == null) {
            // Go back to donor home if the foodId is invalid
            Toast.makeText(this, "Invalid Food", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RecipientFoodItemActivity.this, DonorHomeActivity.class);
            startActivity(intent);
            return;
        }

        authHelper = new AuthHelper(this);
        currentUser = authHelper.getCurrentUser();

        fetchPendingRequests();
        donor = dbHelper.getUser(item.getDonorId());

        // FoodItem
        imgFoodItem = findViewById(R.id.imgFoodItem);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtFoodCategory = findViewById(R.id.txtFoodCategory);
        txtFoodQuantity = findViewById(R.id.txtFoodQuantity);
        txtFoodExpiry = findViewById(R.id.txtFoodExpiry);
        txtFoodAvailableFrom = findViewById(R.id.txtFoodAvailableFrom);
        txtFoodAvailableTo = findViewById(R.id.txtFoodAvailableTo);
        txtFoodAvailableType = findViewById(R.id.txtFoodAvailableType);

        // Set values to Food Item
        String foodItemImageFile = item.getImageKey();

        setPhoto(foodItemImageFile, imgFoodItem, true);

        txtFoodName.setText(item.getName());
        txtFoodCategory.setText(item.getFoodCategory().toString());
        txtFoodQuantity.setText(item.getQuantity());
        txtFoodExpiry.setText(item.getExpiry());

        txtFoodAvailableFrom.setText(item.getFormattedAvailableFrom());
        txtFoodAvailableTo.setText(item.getFormattedAvailableTo());

        // Issue #35. pick up and delivery options are exclusive each other
        txtFoodAvailableType.setText(item.isPickupAvailable() ? "Available by Pick-Up" : "Available by delivery");

        // Requests
        btnRequest = findViewById(R.id.btnRequest);
        if (!item.isActive() || item.isReserved())  {
            // disable button
            disableRequest("Not Available");
        } else if (requests != null && !requests.isEmpty()) {
            disableRequest("Request Pending");
        }

        // donor
        imgDonor = findViewById(R.id.imgDonor);
        txtDonorName = findViewById(R.id.txtDonorName);
        txtDonorPhone = findViewById(R.id.txtDonorPhone);
        txtDonorPostalAddress = findViewById(R.id.txtDonorPostalAddress);
        txtDonorPostalCode = findViewById(R.id.txtDonorPostalCode);

        // donor set values
        String donorImageFile = donor.getImageKey();
        setPhoto(donorImageFile, imgDonor, false);
        
        txtDonorName.setText(donor.getName());
        txtDonorPhone.setText(donor.getPhone());
        txtDonorPostalAddress.setText(donor.getPostalAddress());
        txtDonorPostalCode.setText(donor.getPostalCode());
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
            // Donor profile image fallback (could be a different default if desired)
            Log.d("PhotoPicker", "No donor photo, using layout default");
        }
    }

    private void fetchPendingRequests() {
        Log.d("RecipientFoodItemActivity.fetchPendingReqs", "item id: " + item.getId() + ", recipient id: " + currentUser.getId());
        requests = dbHelper.getRequests(null, item.getId(), currentUser.getId(), Instant.now(), RequestStatus.PENDING, null, false);
    }

    private void disableRequest(String btnText) {
        btnRequest.setText(btnText);
        btnRequest.setEnabled(false);
    }

    public void handleRequest(View view) {
        fetchPendingRequests();
        if (!item.isActive()) {
            Toast.makeText(RecipientFoodItemActivity.this, "The food is already not active", Toast.LENGTH_SHORT);
            disableRequest("Not Available");
            return;
        }
        else if (requests != null && !requests.isEmpty()) {
            Toast.makeText(RecipientFoodItemActivity.this, "You have already requested this item", Toast.LENGTH_SHORT);
            disableRequest("Request Pending");
            return;
        }
        Log.d("RecipientFoodItemActivity.handleRequest", requests.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Instant due = item.getAvailableTo() == null
                        ? Instant.now().plusSeconds(60 * 60 * 24 * 3) // three days
                        : item.getAvailableTo().toInstant();
                long id = dbHelper.createRequest(item.getId(), currentUser.getId(), due, RequestStatus.PENDING, null);
                if (id > 0) {
                    disableRequest("Request Pending");
                    Toast.makeText(RecipientFoodItemActivity.this, "Request Created", Toast.LENGTH_SHORT);
                    Intent intent = new Intent(RecipientFoodItemActivity.this, RecipientHomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RecipientFoodItemActivity.this, "Failed to create a request", Toast.LENGTH_SHORT);
                }
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", null);
        builder.setMessage("You are going to request this item.");
        builder.show();
    }
}