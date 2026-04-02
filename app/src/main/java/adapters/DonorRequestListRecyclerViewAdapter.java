package adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;

import activities.DonorRequestActivity;
import models.FoodItem;
import models.Request;
import models.User;
import utils.ImageServer;

public class DonorRequestListRecyclerViewAdapter extends RecyclerView.Adapter<DonorRequestListRecyclerViewAdapter.ViewHolder> {
    ArrayList<Request> requests;
    public DonorRequestListRecyclerViewAdapter(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public DonorRequestListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_donor_request_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorRequestListRecyclerViewAdapter.ViewHolder holder, int position) {
        Request item = requests.get(position);
        User recipient = item.getRecipient();
        FoodItem foodItem = item.getFoodItem();

        String recipientImgKey = recipient.getImageKey();
        ImageServer imgServer = new ImageServer(holder.itemView.getContext());
        if (recipientImgKey != null && !recipientImgKey.isEmpty()) {
            Bitmap bm = imgServer.loadImage(recipientImgKey);
            holder.imgRecipient.setImageBitmap(bm);
        }

        String foodImgKey = foodItem.getImageKey();
        if (foodImgKey != null && !foodImgKey.isEmpty()) {
            Bitmap bm = imgServer.loadImage(foodImgKey);
            holder.imgFoodItem.setImageBitmap(bm);
        }

        holder.txtFoodName.setText(item.getFoodItem().getName());
        holder.txtRequestStatus.setText(item.getStatus().name());

        String dueText = foodItem.isPickupAvailable() ? "Pick up by: " : "Delivery expected by: ";
        dueText += item.getFormattedDue();
        holder.txtRequestDue.setText(dueText);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                Intent intent = new Intent(holder.itemView.getContext(), DonorRequestActivity.class);
                intent.putExtra(DonorRequestActivity.EXTRA_REQ_ID, item.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRecipient, imgFoodItem;
        TextView txtFoodName, txtRequestStatus, txtRequestDue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgRecipient = itemView.findViewById(R.id.imgRecipient);
            imgFoodItem = itemView.findViewById(R.id.imgFoodItem);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
            txtRequestDue = itemView.findViewById(R.id.txtRequestDue);
        }
    }
}
