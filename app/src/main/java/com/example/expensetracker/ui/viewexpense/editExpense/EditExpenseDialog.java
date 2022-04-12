package com.example.expensetracker.ui.viewexpense.editExpense;

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
import com.example.expensetracker.databinding.DialogEditExpenseBinding;
import com.example.expensetracker.model.ExpenseType;
import com.example.expensetracker.ui.viewtrip.OnAddEditExpenseListener;

import org.json.JSONException;

public class EditExpenseDialog extends DialogFragment  {
    Long expenseId;
    DialogEditExpenseBinding binding;
    EditExpenseViewModel editExpenseViewModel;
    private OnAddEditExpenseListener onAddEditExpenseListener;

    public EditExpenseDialog(Long expenseId, OnAddEditExpenseListener onAddEditExpenseListener) {
        super();
        this.expenseId = expenseId;
        this.onAddEditExpenseListener = onAddEditExpenseListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        editExpenseViewModel = new ViewModelProvider(getActivity()).get(EditExpenseViewModel.class);
        editExpenseViewModel.expenseId = expenseId;
        binding = DialogEditExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editExpenseViewModel.expenseLive.observe(getViewLifecycleOwner(), expense -> {
            editExpenseViewModel.amount = expense.getAmount();
            editExpenseViewModel.description = expense.getDescription();
            editExpenseViewModel.expenseType = expense.getType();

            setBinding();
        });
        setBinding();

        binding.expenseTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.stay_option: {
                    editExpenseViewModel.expenseType = ExpenseType.STAY;
                    break;
                }
                case R.id.transport_option: {
                    editExpenseViewModel.expenseType = ExpenseType.TRANSPORT;
                    break;
                }
                case R.id.meal_option: {
                    editExpenseViewModel.expenseType = ExpenseType.MEAL;
                    break;
                }
                default: {
                    editExpenseViewModel.expenseType = ExpenseType.OTHER;
                    break;
                }
            }
        });

        binding.buttonEditExpense.setOnClickListener(l -> {
            String expense = TextUtils.isEmpty(binding.editExpenseEditText.getText())
                    ? "0"
                    : binding.editExpenseEditText.getText().toString();
            double amount = Double.parseDouble(expense);
            String description = binding.expenseDescriptionEditText.getText().toString();
            try {
                editExpenseViewModel.editExpenseInTrip(
                        description,
                        editExpenseViewModel.expenseType,
                        amount).subscribe(expense1 -> {
                            if (expense1 != null) {
                                Toast.makeText(getContext(), "Expense successfully updated", Toast.LENGTH_SHORT).show();
                                onAddEditExpenseListener.onAcceptClick();
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), "Error while updating expense", Toast.LENGTH_SHORT).show();
                            }
                }, error -> Toast.makeText(getContext(), "Error while updating expense", Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        binding.buttonCancelExpense.setOnClickListener(l -> {
            // TODO after dismiss trigger load data in tripview
            dismiss();
        });
    }

    private void setBinding() {
        if (editExpenseViewModel.amount != null) {
            binding.editExpenseEditText.setText(editExpenseViewModel.amount.toString());
        }

        if (editExpenseViewModel.expenseType != null) {
            switch (editExpenseViewModel.expenseType) {
                case STAY:
                    binding.expenseTypeRadioGroup.check(R.id.stay_option);
                    break;
                case TRANSPORT:
                    binding.expenseTypeRadioGroup.check(R.id.transport_option);
                    break;
                case MEAL:
                    binding.expenseTypeRadioGroup.check(R.id.meal_option);
                    break;
                default:
                    binding.expenseTypeRadioGroup.check(R.id.other_option);
                    break;
            }
        }

        binding.expenseDescriptionEditText.setText(editExpenseViewModel.description);
    }

    @Override
    public void onResume() {
        super.onResume();
        editExpenseViewModel.getExpenseById();
    }
}
