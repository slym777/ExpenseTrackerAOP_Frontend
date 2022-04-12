package com.example.expensetracker.ui.viewtrip.personalexpense;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.ExpenseApi;
import com.example.expensetracker.model.Expense;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class PersonalExpenseViewModel extends ViewModel {
    public Long tripId;
    public MutableLiveData<List<Expense>> expensesLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getPersonalExpenses(){
        disposableLinkedList.add(ExpenseApi.getExpensesByTripId(tripId, false).subscribe(list -> {
            expensesLiveList.postValue(list);
        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
