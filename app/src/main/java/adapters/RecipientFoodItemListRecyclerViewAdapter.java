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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import activities.RecipientFoodItemActivity;
import models.FoodItem;
import utils.ImageServer;

public class RecipientFoodItemListRecyclerViewAdapter extends RecyclerView.Adapter<RecipientFoodItemListRecyclerViewAdapter.ViewHolder> {
    ArrayList<FoodItem> items;

    public RecipientFoodItemListRecyclerViewAdapter(ArrayList<FoodItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecipientFoodItemListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_recipient_food_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);
        String imageKey = item.getImageKey();
        
        if (imageKey != null && !imageKey.isEmpty()) {
            Bitmap bm = new ImageServer(holder.itemView.getContext()).loadImage(item.getImageKey());
            if (bm != null) {
                holder.foodImg.setImageBitmap(bm);
            } else {
                holder.foodImg.setImageResource(R.drawable.item_static);
            }
        } else {
            holder.foodImg.setImageResource(R.drawable.item_static);
        }

        holder.txtFoodName.setText(item.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        holder.txtAddedAt.setText("Added at: " + item.getAddedAt());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), RecipientFoodItemActivity.class);
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
        ImageView foodImg;
        TextView txtFoodName, txtAddedAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImg = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtAddedAt = itemView.findViewById(R.id.txtFoodAddedAt);
        }
    }
}
