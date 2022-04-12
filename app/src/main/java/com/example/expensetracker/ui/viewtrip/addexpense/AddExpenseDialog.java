package com.example.expensetracker.ui.viewtrip.addexpense;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.DialogAddExpenseBinding;
import com.example.expensetracker.model.ExpenseType;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.ui.viewtrip.OnAddEditExpenseListener;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class AddExpenseDialog extends DialogFragment {
    private final Trip trip;
    DialogAddExpenseBinding binding;
    AddExpenseViewModel addExpenseViewModel;
    private boolean comesFromTripView;
    private OnAddEditExpenseListener onAddEditExpenseListener;

    public AddExpenseDialog(Trip trip, OnAddEditExpenseListener onAddEditExpenseListener) {
        super();
        this.trip = trip;
        this.onAddEditExpenseListener = onAddEditExpenseListener;
        comesFromTripView = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        addExpenseViewModel = new ViewModelProvider(requireActivity()).get(AddExpenseViewModel.class);
        if (comesFromTripView) {
            addExpenseViewModel.resetValues();
            comesFromTripView = false;
        }
        binding = DialogAddExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addExpenseViewModel.tripId = trip.getId();

        binding.isGroupCheckbox.setOnClickListener(l -> {
            if (!binding.isGroupCheckbox.isChecked()) {
                binding.members.setVisibility(View.GONE);
                addExpenseViewModel.isGroupExpense = false;
            } else {
                binding.members.setVisibility(View.VISIBLE);
                addExpenseViewModel.isGroupExpense = true;
            }
        });

        addExpenseViewModel.selectedUserList.observe(getViewLifecycleOwner(), users -> {
            binding.nrParticipants.setText(String.format("%d", users.size()));
        });

        binding.expenseTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.stay_option: {
                    addExpenseViewModel.expenseType = ExpenseType.STAY;
                    break;
                }
                case R.id.transport_option: {
                    addExpenseViewModel.expenseType = ExpenseType.TRANSPORT;
                    break;
                }
                case R.id.meal_option: {
                    addExpenseViewModel.expenseType = ExpenseType.MEAL;
                    break;
                }
                default: {
                    addExpenseViewModel.expenseType = ExpenseType.OTHER;
                    break;
                }
            }
        });

        binding.members.setOnClickListener(l -> showAddMembersDialog());

        binding.buttonAddExpense.setOnClickListener(l -> createExpense());

        binding.buttonCancelExpense.setOnClickListener(l -> dismiss());
    }

    private void createExpense() {
        if (addExpenseViewModel.isGroupExpense != null && addExpenseViewModel.isGroupExpense) {
            if ( addExpenseViewModel.selectedUserList.getValue() == null ||
                    addExpenseViewModel.selectedUserList.getValue().isEmpty()) {
                Toast.makeText(getContext(), "Cannot add a group expense with no members", Toast.LENGTH_SHORT).show();
                return ;
            }

            if (addExpenseViewModel.selectedUserList.getValue().size() == 1 &&
                addExpenseViewModel.selectedUserList.getValue().get(0).equals(SharedPreferencesUtils.getProfileDetails())) {
                Toast.makeText(getContext(), "Did you mean to create a personal payment?", Toast.LENGTH_SHORT).show();
                return ;
            }
        }

        String expense = TextUtils.isEmpty(binding.addExpenseEditText.getText())
                ? "0"
                : binding.addExpenseEditText.getText().toString();
        double amount = Double.parseDouble(expense);
        String description = binding.expenseDescriptionEditText.getText().toString();
        try {
            addExpenseViewModel.createExpenseInTrip(
                    description,
                    addExpenseViewModel.expenseType,
                    amount,
                    addExpenseViewModel.selectedUserList.getValue(),
                    addExpenseViewModel.isGroupExpense
            ).subscribe(bool -> {
                        if (bool) {
                            Toast.makeText(getContext(), "Expense successfully created", Toast.LENGTH_SHORT).show();
                            onAddEditExpenseListener.onAcceptClick();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error while creating expense", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showAddMembersDialog() {
        ChooseMembersExpenseDialog dialog = new ChooseMembersExpenseDialog(trip);
        dialog.show(getChildFragmentManager(), "ChooseMembersExpenseDialog");
    }
}
