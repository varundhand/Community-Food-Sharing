package adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.util.ArrayList;

import models.FoodItem;

public class FoodItemListRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemListRecyclerViewAdapter.ViewHolder> {
    Class<?> detailClass;
    ArrayList<FoodItem> items;

    public FoodItemListRecyclerViewAdapter(ArrayList<FoodItem> items, Class<?> detailClass) {
        this.items = items;
        this.detailClass = detailClass;
    }

    @NonNull
    @Override
    public FoodItemListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 1. INFLATE THE NEW LAYOUT
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_my_food_listing, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);

        // 2. BIND THE DATA
        holder.txtFoodName.setText(item.getName());

        holder.txtQuantity.setText(item.getQuantity());
        holder.txtExpiry.setText(item.getExpiry() == null || item.getExpiry().isEmpty() ? "N/A" : item.getExpiry());

        boolean isActive = item.isActive();
        holder.txtCategoryBadge.setText(isActive ? "ACTIVE" : "COMPLETED");


        // 3. HANDLE CLICKS
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), detailClass);
                intent.putExtra("FoodItemId", item.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 4. DECLARE THE NEW VARIABLES
        TextView txtCategoryBadge, txtDeliveryType, txtFoodName, txtQuantity, txtExpiry;
        ImageView imgDeliveryIcon;
        LinearLayout badgeDeliveryType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 5. LINK TO THE NEW XML IDs
            txtCategoryBadge = itemView.findViewById(R.id.txtCategoryBadge);
            txtDeliveryType = itemView.findViewById(R.id.txtDeliveryType);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtExpiry = itemView.findViewById(R.id.txtExpiry);
            imgDeliveryIcon = itemView.findViewById(R.id.imgDeliveryIcon);
            badgeDeliveryType = itemView.findViewById(R.id.badgeDeliveryType);
        }
    }
}