package adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodshare.R;

import java.time.Instant;
import java.util.ArrayList;

import activities.RecipientRequestActivity;
import database.DatabaseHelper;
import models.Reminder;

public class RecipientReminderListRecyclerViewAdapter extends RecyclerView.Adapter<RecipientReminderListRecyclerViewAdapter.ViewHolder> {
    ArrayList<Reminder> reminders;

    public RecipientReminderListRecyclerViewAdapter(ArrayList<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_recipient_reminder_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder item = reminders.get(position);
        String titlePrefix = "";

        if (!item.isRead()) {
            titlePrefix += "[new] ";
        }
        holder.txtReminderTitle.setText(titlePrefix + item.getTitle());

        holder.txtReminderContent.setText(item.getContent());
        holder.txtReminderAddedAt.setText(item.getFormattedAddedAt());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set read
                try (DatabaseHelper dbHelper = new DatabaseHelper(holder.itemView.getContext())) {
                    dbHelper.setReadToReminder(item.getId(), Instant.now());
                }
                Intent intent = new Intent(holder.itemView.getContext(), RecipientRequestActivity.class);
                intent.putExtra(RecipientRequestActivity.EXTRA_REQ_ID, item.getRequestId());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtReminderTitle, txtReminderContent, txtReminderAddedAt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtReminderTitle = itemView.findViewById(R.id.txtReminderTitle);
            txtReminderContent = itemView.findViewById(R.id.txtReminderContent);
            txtReminderAddedAt = itemView.findViewById(R.id.txtReminderAddedAt);
        }
    }
}
