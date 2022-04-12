package com.example.expensetracker.ui.trips;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Trip;
import com.example.expensetracker.utils.SharedPreferencesUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class TripViewModel extends ViewModel {

    public MutableLiveData<List<Trip>> tripLiveList = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getAllTrips(){
        disposableLinkedList.add(TripApi.getAllTrips().subscribe(list -> {
            List<Trip> userTrips = list.stream()
                    .filter(t -> t.getUsers().contains(SharedPreferencesUtils.getProfileDetails())).collect(Collectors.toList());
            tripLiveList.postValue(userTrips);
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