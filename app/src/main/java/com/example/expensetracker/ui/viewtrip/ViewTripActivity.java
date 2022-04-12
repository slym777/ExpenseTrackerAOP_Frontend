package com.example.expensetracker.ui.viewtrip;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.ActivityTripViewBinding;
import com.example.expensetracker.ui.viewtrip.groupexpense.GroupExpensesFragment;
import com.example.expensetracker.ui.viewtrip.personalexpense.PersonalExpensesFragment;
import com.example.expensetracker.ui.viewtrip.tripinfo.TripInfoFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.expensetracker.utils.ConstantsUtils.TRIP_ID_EXTRA;

public class ViewTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTripViewBinding binding = ActivityTripViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.trip_view_fragment_host);
        if (!navController.navigateUp()){
            super.onBackPressed();
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}
