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

import activities.EditFoodItemActivity;
import models.FoodItem;
import utils.ImageServer;

public class FoodItemListRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemListRecyclerViewAdapter.ViewHolder> {
    ArrayList<FoodItem> items;

    public FoodItemListRecyclerViewAdapter(ArrayList<FoodItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FoodItemListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_donor_food_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = items.get(position);
        String imageKey = item.getImageKey();
        if (imageKey != null && !imageKey.isEmpty()) {
            Bitmap bm = new ImageServer(holder.itemView.getContext()).loadImage(item.getImageKey());
            holder.foodImg.setImageBitmap(bm);
        }

        boolean isActive = item.isActive();
        holder.txtActive.setText(isActive ? "Active" : "Completed");
        holder.txtFoodName.setText(item.getName());

        // format ZonedDateTime
        // reference: https://www.baeldung.com/java-format-zoned-datetime-string#date_to_string-1
        // reference: https://www.baeldung.com/java-datetimeformatter#formatStyle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        holder.txtAddedAt.setText("Added at: " + item.getAddedAt());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), EditFoodItemActivity.class);
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
        TextView txtActive, txtFoodName, txtAddedAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodImg = itemView.findViewById(R.id.imgFood);
            txtActive = itemView.findViewById(R.id.txtActive);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtAddedAt = itemView.findViewById(R.id.txtFoodAddedAt);
        }
    }
}
