package com.example.expensetracker.ui.viewtrip;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentTripViewBinding;
import com.example.expensetracker.ui.addtrip.AddTripActivity;
import com.example.expensetracker.ui.viewtrip.addexpense.AddExpenseDialog;
import com.example.expensetracker.ui.viewtrip.groupexpense.GroupExpenseViewModel;
import com.example.expensetracker.ui.viewtrip.groupexpense.GroupExpensesFragment;
import com.example.expensetracker.ui.viewtrip.personalexpense.PersonalExpenseViewModel;
import com.example.expensetracker.ui.viewtrip.personalexpense.PersonalExpensesFragment;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripInfoFragment;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripInfoViewModel;
import com.example.expensetracker.utils.BaseApp;

import java.util.ArrayList;
import java.util.List;

import static com.example.expensetracker.utils.ConstantsUtils.TRIP_ID_EXTRA;

import org.json.JSONException;

import timber.log.Timber;

public class ViewTripFragment extends Fragment implements OnAddEditExpenseListener {

    private FragmentTripViewBinding binding;
    private ViewTripPagerAdapter pagerAdapter;
    private TripInfoViewModel tripInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        tripInfoViewModel = new ViewModelProvider(requireActivity()).get(TripInfoViewModel.class);

        Long tripId = getActivity().getIntent().getLongExtra(TRIP_ID_EXTRA, -1);
        tripInfoViewModel.tripId = tripId;

        binding = FragmentTripViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        pagerAdapter = new ViewTripPagerAdapter(getChildFragmentManager()
                , tripInfoViewModel.tripId);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.viewpager.setCurrentItem(0);

        binding.addExpenseButton.setOnClickListener(l -> showAddExpenseDialog());

        tripInfoViewModel.tripLive.observe(getViewLifecycleOwner(), trip -> {
            ((ViewTripActivity) getActivity()).setActionBarTitle(trip.getName());

            binding.tripName.setText(trip.getName());
            binding.nrMembers.setText(trip.getGroupSize().toString() + " members");

            if (!TextUtils.isEmpty(trip.getAvatarUri())) {
                Glide.with(BaseApp.context)
                        .load(trip.getAvatarUri())
                        .centerCrop()
                        .placeholder(R.drawable.progress_animation)
                        .into(binding.tripAvatar);
            } else {
                Glide.with(BaseApp.context).clear(binding.tripAvatar);
                binding.tripAvatar.setImageResource(R.drawable.default_trip_back);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.trip_view_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_button:
                handleDeleteTrip();
                return true;
            case R.id.edit_button:
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_trip_view_to_navigation_edit_trip_view);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class ViewTripPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();

        public List<Fragment> getFragments() {
            return fragments;
        }

        public ViewTripPagerAdapter(@NonNull FragmentManager fm, Long tripId) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            fragments.add(new GroupExpensesFragment(tripId));
            fragments.add(new PersonalExpensesFragment(tripId));
            fragments.add(new TripInfoFragment(tripId));
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Group Expenses";
                case 1: return "Personal Expenses";
                case 2: return "Trip Info";
                default: throw new IllegalArgumentException("Exception");
            }
        }
    }

    private void handleDeleteTrip() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete trip \"" + tripInfoViewModel.tripLive.getValue().getName() + "\"")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> tripInfoViewModel.deleteTripById().subscribe(
                        bool -> {
                            if (bool) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);

                                Toast.makeText(getContext(), "Trip deleted successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Could not delete trip", Toast.LENGTH_SHORT).show();
                            }
                        }, err -> Toast.makeText(getContext(), err.getMessage(), Toast.LENGTH_SHORT).show()
                ))
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddExpenseDialog() {
        AddExpenseDialog dialog = new AddExpenseDialog(tripInfoViewModel.tripLive.getValue(), this);
        dialog.show(getChildFragmentManager(), "AddExpenseDialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        tripInfoViewModel.getTripById();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAcceptClick() {
        pagerAdapter.getFragments().get(0).onResume();
        pagerAdapter.getFragments().get(1).onResume();
    }
}
