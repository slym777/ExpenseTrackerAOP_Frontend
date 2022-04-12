package com.example.expensetracker.ui.viewexpense.editExpense;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.ExpenseType;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class EditExpenseViewModel extends ViewModel {
    Long expenseId;
    String description;
    ExpenseType expenseType;
    Double amount;

    public MutableLiveData<Expense> expenseLive = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getExpenseById() {
        disposableLinkedList.add(ExpenseApi.getExpenseByExpenseId(expenseId).subscribe(expense -> {
            expenseLive.postValue(expense);
        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

    public BehaviorSubject<Expense> editExpenseInTrip(String description, ExpenseType expenseType, Double amount) throws JSONException {
        Expense expenseRequest = new Expense(description, expenseType, amount);
        return ExpenseApi.editExpense(expenseId, expenseRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
