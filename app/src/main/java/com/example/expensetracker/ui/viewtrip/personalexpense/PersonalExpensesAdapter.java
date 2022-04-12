package com.example.expensetracker.ui.viewtrip.personalexpense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.databinding.ViewPersonalExpenseBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.viewtrip.OnClickExpenseListener;

import java.util.List;

public class PersonalExpensesAdapter extends RecyclerView.Adapter<PersonalExpensesAdapter.PersonalExpensesHolder> {
    private List<Expense> mExpenses;
    private OnClickExpenseListener onClickExpenseListener;

    public PersonalExpensesAdapter(List<Expense> mExpenses, OnClickExpenseListener onClickPersonalExpenseListener) {
        this.mExpenses = mExpenses;
        this.onClickExpenseListener = onClickPersonalExpenseListener;
    }

    public List<Expense> getPersonalExpenses() {
        return mExpenses;
    }

    public void updateRecyclerView(List<Expense> expenseList){
        mExpenses.clear();
        mExpenses.addAll(expenseList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonalExpensesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewPersonalExpenseBinding binding = ViewPersonalExpenseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PersonalExpensesHolder(binding, onClickExpenseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalExpensesHolder holder, int position) {
        Expense expense = mExpenses.get(position);
        holder.binding.typeText.setText(expense.getType().name());
        holder.binding.amount.setText(String.format("%.2f$", expense.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public class PersonalExpensesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewPersonalExpenseBinding binding;
        OnClickExpenseListener onClickPersonalExpenseListener;

        public PersonalExpensesHolder(ViewPersonalExpenseBinding binding, OnClickExpenseListener onClickPersonalExpenseListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClickPersonalExpenseListener = onClickPersonalExpenseListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Expense expense = mExpenses.get(getAdapterPosition());
            onClickPersonalExpenseListener.onGroupExpenseClick(expense.getId());
        }

    }

}
