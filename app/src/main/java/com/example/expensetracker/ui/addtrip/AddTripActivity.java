package com.example.expensetracker.ui.addtrip;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;

public class AddTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        getSupportActionBar().setTitle("Add Trip");
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.add_trip_fragment_host);
        if (!navController.navigateUp()){
            super.onBackPressed();
        }
    }

}
