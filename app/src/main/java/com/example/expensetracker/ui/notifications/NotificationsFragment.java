package com.example.expensetracker.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;
    private NotificationAdapter notificationAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.notifRecyclerView.setHasFixedSize(true);
        binding.notifRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationAdapter = new NotificationAdapter(new ArrayList<>());
        binding.notifRecyclerView.setAdapter(notificationAdapter);

        notificationsViewModel.notificationLiveList.observe(getViewLifecycleOwner(), notifications -> {
            notificationAdapter.updateRecyclerView(notifications);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        notificationsViewModel.getAllNotifications();
    }
}
