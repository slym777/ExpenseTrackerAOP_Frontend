package com.example.expensetracker.ui.viewtrip.addexpense;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ViewChooseMemberExpenseBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.BaseApp;

import java.util.List;
import java.util.stream.Collectors;

public class ChooseMembersExpenseAdapter extends RecyclerView.Adapter<ChooseMembersExpenseAdapter.UserViewHolder> {
    private List<User> mUsers;

    public ChooseMembersExpenseAdapter(List<User> mUsers) {
        this.mUsers = mUsers;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public void updateRecyclerView(List<User> userList){
        mUsers.clear();
        mUsers.addAll(userList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewChooseMemberExpenseBinding binding = ViewChooseMemberExpenseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(binding);
    }

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
        public ViewChooseMemberExpenseBinding binding;

        public UserViewHolder(ViewChooseMemberExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final User user) {
            if (user.isSelected()) {
                binding.vsuUserCard.setChecked(true);
            } else {
                binding.vsuUserCard.setChecked(false);
            }

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

            binding.vsuUserCard.setOnClickListener(v -> {
                user.changeSelectedState();
                binding.vsuUserCard.toggle();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<User> getSelected() {
        return mUsers.stream()
                .filter(User::isSelected)
                .collect(Collectors.toList());
    }

}
