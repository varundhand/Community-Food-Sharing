package adapters;

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

import models.FoodItem;
import models.Request;
import utils.ImageServer;

public class RecipientRequestListRecyclerViewAdapter extends RecyclerView.Adapter<RecipientRequestListRecyclerViewAdapter.ViewHolder> {
    ArrayList<Request> requests;

    public RecipientRequestListRecyclerViewAdapter(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RecipientRequestListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_recipient_request_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientRequestListRecyclerViewAdapter.ViewHolder holder, int position) {
        Request request = requests.get(position);
        FoodItem foodItem = request.getFoodItem();
        String foodItemImageKey = foodItem.getImageKey();
        if (foodItemImageKey != null && !foodItemImageKey.isEmpty()) {
            Bitmap bm = new ImageServer(holder.itemView.getContext()).loadImage(foodItemImageKey);
            holder.imgFoodItem.setImageBitmap(bm);
        }

        holder.txtRequestStatus.setText(request.getStatus().name());
        holder.txtFoodItemName.setText(foodItem.getName());
        holder.txtRequestDue.setText("Due: " + request.getFormattedDue());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoodItem;
        TextView txtRequestStatus, txtFoodItemName, txtRequestDue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFoodItem = itemView.findViewById(R.id.imgFoodItem);
            txtRequestStatus = itemView.findViewById(R.id.txtRequestStatus);
            txtFoodItemName = itemView.findViewById(R.id.txtFoodItemName);
            txtRequestDue = itemView.findViewById(R.id.txtRequestDue);
        }
    }
}
