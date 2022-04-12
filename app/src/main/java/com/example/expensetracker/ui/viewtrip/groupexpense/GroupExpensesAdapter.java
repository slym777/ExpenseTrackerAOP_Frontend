package com.example.expensetracker.ui.viewtrip.groupexpense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.databinding.ViewGroupExpenseBinding;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.ui.viewtrip.OnClickExpenseListener;

import java.util.List;

public class GroupExpensesAdapter extends RecyclerView.Adapter<GroupExpensesAdapter.GroupExpensesHolder> {
    private List<Expense> mExpenses;
    private OnClickExpenseListener onClickGroupExpenseListener;

    public GroupExpensesAdapter(List<Expense> mExpenses, OnClickExpenseListener onClickGroupExpenseListener) {
        this.mExpenses = mExpenses;
        this.onClickGroupExpenseListener = onClickGroupExpenseListener;
    }

    public List<Expense> getGroupExpenses() {
        return mExpenses;
    }

    public void updateRecyclerView(List<Expense> expenseList){
        mExpenses.clear();
        mExpenses.addAll(expenseList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GroupExpensesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewGroupExpenseBinding binding = ViewGroupExpenseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GroupExpensesHolder(binding, onClickGroupExpenseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupExpensesHolder holder, int position) {
        Expense expense = mExpenses.get(position);
        holder.binding.typeText.setText(expense.getType().name());
        holder.binding.nrContributors.setText(expense.getCreditors().size() + " contributors");
        holder.binding.amount.setText(String.format("%.2f$", expense.getAmount()));
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    public class GroupExpensesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewGroupExpenseBinding binding;
        OnClickExpenseListener onClickGroupExpenseListener;

        public GroupExpensesHolder(ViewGroupExpenseBinding binding, OnClickExpenseListener onClickGroupExpenseListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onClickGroupExpenseListener = onClickGroupExpenseListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Expense expense = mExpenses.get(getAdapterPosition());
            onClickGroupExpenseListener.onGroupExpenseClick(expense.getId());
        }

    }

}
