package com.example.expensetracker.ui.viewexpense;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Trip;

import org.json.JSONException;

import java.util.LinkedList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class ExpenseViewModel extends ViewModel {
    public Long expenseId;
    public MutableLiveData<Expense> expenseLive = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getExpense(){
        disposableLinkedList.add(ExpenseApi.getExpenseByExpenseId(expenseId).subscribe(
                expense -> expenseLive.postValue(expense),
                error -> {
                    Timber.e(error);
                    errorLiveMsg.postValue(error.getLocalizedMessage());
                })
        );
    }

    public BehaviorSubject<Boolean> deleteExpenseById() throws JSONException {
        return ExpenseApi.deleteExpenseInTrip(expenseId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
