package com.example.expensetracker.ui.notifications;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.databinding.ViewNotificationsListItemBinding;
import com.example.expensetracker.model.Notification;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> mNotifications;

    public NotificationAdapter(List<Notification> mNotifications) {
        this.mNotifications = mNotifications;
    }

    public void updateRecyclerView(List<Notification> notificationList){
        mNotifications.clear();
        mNotifications.addAll(notificationList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewNotificationsListItemBinding binding = ViewNotificationsListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = mNotifications.get(position);

        holder.binding.notifMessage.setText(notification.getDescription());

        ZonedDateTime zdt = ZonedDateTime.ofInstant(notification.getCreatedDate().toInstant(), ZoneId.systemDefault());
        Calendar calendar = GregorianCalendar.from(zdt);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = new SimpleDateFormat("MMM").format(calendar.getTime());
        String timeStr = String.format("%02d:%02d", hour, minute);
        String dateStr = month + " " + day;

        holder.binding.notifDate.setText(dateStr);
        holder.binding.notifTime.setText(timeStr);
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        public ViewNotificationsListItemBinding binding;

        public NotificationViewHolder(ViewNotificationsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
