package com.example.expensetracker.ui.viewtrip.tripinfo;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentTripInfoBinding;
import com.example.expensetracker.ui.viewtrip.personalexpense.PersonalExpensesAdapter;
import com.example.expensetracker.utils.BaseApp;

import java.util.ArrayList;

public class TripInfoFragment extends Fragment {

    private TripInfoViewModel tripInfoViewModel;
    private TripMembersAdapter tripMembersAdapter;
    private FragmentTripInfoBinding binding;
    private Long tripId;
    private Boolean isMemberListExpanded;

    public TripInfoFragment(Long tripId) {
        super();
        this.tripId = tripId;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tripInfoViewModel = new ViewModelProvider(getActivity()).get(TripInfoViewModel.class);
        binding = FragmentTripInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.membersRecyclerView.setHasFixedSize(true);
        binding.membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripMembersAdapter = new TripMembersAdapter(new ArrayList<>());
        binding.membersRecyclerView.setAdapter(tripMembersAdapter);

        isMemberListExpanded = false;

        tripInfoViewModel.tripLive.observe(getViewLifecycleOwner(), trip -> {
            binding.descValue.setText(trip.getDescription());
            binding.locationValue.setText(trip.getLocation());
            binding.nrContributors.setText(trip.getGroupSize().toString());
            tripMembersAdapter.updateRecyclerView(trip);
        });

        binding.memberListExpand.setOnClickListener(v -> {
            if (isMemberListExpanded) {
                binding.membersRecyclerView.setVisibility(View.VISIBLE);
                binding.imageExpand.setImageDrawable(getResources().getDrawable(R.drawable.expand2_icon));
                isMemberListExpanded = false;
            } else {
                binding.membersRecyclerView.setVisibility(View.GONE);
                binding.imageExpand.setImageDrawable(getResources().getDrawable(R.drawable.expand_icon));
                isMemberListExpanded = true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tripInfoViewModel.getTripById();
    }
}
