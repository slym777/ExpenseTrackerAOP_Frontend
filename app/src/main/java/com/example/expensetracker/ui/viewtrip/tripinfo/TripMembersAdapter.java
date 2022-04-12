package com.example.expensetracker.ui.viewtrip.tripinfo;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ViewTripMembersBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.BaseApp;

import java.util.List;
import java.util.stream.Collectors;

public class TripMembersAdapter extends RecyclerView.Adapter<TripMembersAdapter.UserViewHolder> {
    private List<User> mUsers;
    private Trip trip;

    public TripMembersAdapter(List<User> mUsers) {
        this.mUsers = mUsers;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void updateRecyclerView(Trip trip){
        mUsers.clear();
        mUsers.addAll(trip.getUsers());
        this.trip = trip;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewTripMembersBinding binding = ViewTripMembersBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        public ViewTripMembersBinding binding;

        public UserViewHolder(ViewTripMembersBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(final User user) {
            Double amount = trip.getExpenses()
                    .stream()
                    .filter(Expense::getIsGroupExpense)
                    .filter(e -> e.getCreditors().contains(user))
                    .map(e -> e.getAmount() / e.getCreditors().size())
                    .reduce(0.0, Double::sum);

            binding.amount.setText(String.format("%.2f$", amount));

            binding.vsuUserName.setText(user.getFullName());

            if (!TextUtils.isEmpty(user.getAvatarUri())) {
                Glide.with(BaseApp.context)
                        .load(user.getAvatarUri())
                        .centerCrop()
                        .placeholder(R.drawable.progress_animation)
                        .into(binding.vsuUserAvatar);
            }  else {
                Glide.with(BaseApp.context).clear(binding.vsuUserAvatar);
                binding.vsuUserAvatar.setImageResource(R.drawable.default_user_avatar);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<User> getSelected() {
        return mUsers.stream()
                .filter(User::isSelected)
                .collect(Collectors.toList());
    }

}
