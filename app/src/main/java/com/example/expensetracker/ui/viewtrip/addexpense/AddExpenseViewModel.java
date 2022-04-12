package com.example.expensetracker.ui.viewtrip.addexpense;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.ExpenseType;
import com.example.expensetracker.model.User;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class AddExpenseViewModel extends ViewModel {
    Long tripId;
    String description;
    ExpenseType expenseType;
    Double amount;
    Boolean isGroupExpense;

    public List<User> allUserList = new ArrayList<>();
    public MutableLiveData<List<User>> selectedUserList = new MutableLiveData<>();
    public MutableLiveData<List<User>> searchUserLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resetValues() {
        expenseType = null;
        isGroupExpense = null;
        selectedUserList.setValue(new ArrayList<>());
        allUserList.forEach(user -> user.setSelected(false));
    }

    public void setTripUsers(List<User> users) {
        allUserList.clear();
        allUserList.addAll(users);

        for (User user: users) {
            if (selectedUserList.getValue() == null || selectedUserList.getValue().isEmpty()) {
                break;
            }

            for (User selectedUser: selectedUserList.getValue()) {
                if (user.getId().equals(selectedUser.getId())) {
                    user.setSelected(true);
                }
            }
        }

        searchUserLiveList.setValue(users);
    }

    public BehaviorSubject<Boolean> createExpenseInTrip(String description, ExpenseType expenseType, Double amount, List<User> creditors, Boolean isGroupExpense) throws JSONException {
        User debtor = SharedPreferencesUtils.getProfileDetails();
        if (isGroupExpense == null || !isGroupExpense) {
            isGroupExpense = false;
            creditors.clear();
        }

        Expense expenseRequest = new Expense(description, expenseType, amount, debtor, creditors, isGroupExpense);
        return TripApi.createExpense(tripId, expenseRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addMembers() {
        List<User> selectedUsers = allUserList.stream()
                .filter(User::isSelected)
                .collect(Collectors.toList());
        selectedUserList.postValue(selectedUsers);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }

}
