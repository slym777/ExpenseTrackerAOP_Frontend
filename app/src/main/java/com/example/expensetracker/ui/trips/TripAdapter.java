package com.example.expensetracker.ui.trips;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ViewTripBinding;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.BaseApp;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> mTrips;
    private OnClickTripListener onClickTripListener;

    public TripAdapter(List<Trip> mTrips, OnClickTripListener onClickTripListener) {
        this.mTrips = mTrips;
        this.onClickTripListener = onClickTripListener;
    }

    public List<Trip> getTrips() {
        return mTrips;
    }

    public void updateRecyclerView(List<Trip> tripList){
        mTrips.clear();
        mTrips.addAll(tripList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewTripBinding binding = ViewTripBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TripViewHolder(binding, onClickTripListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = mTrips.get(position);
        holder.binding.vtTripName.setText(trip.getName());
        holder.binding.vtNrMembers.setText(trip.getGroupSize().toString());
        if (!TextUtils.isEmpty(trip.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(trip.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.binding.vtTripAvatar);
        } else {
            Glide.with(BaseApp.context).clear(holder.binding.vtTripAvatar);
            holder.binding.vtTripAvatar.setImageResource(R.drawable.default_trip_back);
        }
    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewTripBinding binding;
        OnClickTripListener onHubListener;

        public TripViewHolder(ViewTripBinding binding, OnClickTripListener onHubListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onHubListener = onHubListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Trip trip = mTrips.get(getAdapterPosition());
            onHubListener.onTripClick(trip.getId(), trip.getName());
        }

    }

    public interface OnClickTripListener {
        void onTripClick(Long hubId, String hubName);
    }

}
