package com.example.expensetracker.ui.trips;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.expensetracker.databinding.FragmentTripsBinding;
import com.example.expensetracker.ui.addtrip.AddTripActivity;
import com.example.expensetracker.ui.viewtrip.ViewTripActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import static com.example.expensetracker.utils.ConstantsUtils.TRIP_ID_EXTRA;

public class TripsFragment extends Fragment implements TripAdapter.OnClickTripListener {

    private FragmentTripsBinding binding;
    private TripViewModel tripViewModel;
    private TripAdapter tripAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        binding = FragmentTripsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ftTripsRecyclerView.setHasFixedSize(true);
        binding.ftTripsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        tripAdapter = new TripAdapter(new ArrayList<>(), this);
        binding.ftTripsRecyclerView.setAdapter(tripAdapter);

        binding.ftAddTripButton.setOnClickListener(v -> {
            addTrip();
        });

        tripViewModel.tripLiveList.observe(getViewLifecycleOwner(), trips -> {
            if (trips.isEmpty()) {
                binding.ftTripsRecyclerView.setVisibility(View.GONE);
                binding.emptyView.setVisibility(View.VISIBLE);
            } else {
                binding.ftTripsRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyView.setVisibility(View.GONE);
                tripAdapter.updateRecyclerView(trips);
            }
        });

        tripViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Check connection or retry lo login")
                    .setMessage(str)
                    .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        tripViewModel.getAllTrips();
    }


//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.main_filter_menu, menu);
//    }

    private void addTrip(){
        Intent intent = new Intent(getActivity(), AddTripActivity.class);
        startActivity(intent);
    }

    @Override
    public void onTripClick(Long hubId, String hubName) {
        Intent intent = new Intent(getActivity(), ViewTripActivity.class);
        intent.putExtra(TRIP_ID_EXTRA, hubId);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        tripViewModel.getAllTrips();
    }

}