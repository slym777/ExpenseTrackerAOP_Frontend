package com.example.expensetracker.ui.viewtrip.tripinfo;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.TripApi;
import com.example.expensetracker.model.Trip;

import java.util.LinkedList;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import timber.log.Timber;

public class TripInfoViewModel extends ViewModel {
    public Long tripId;
    public MutableLiveData<Trip> tripLive = new MutableLiveData<>();
    public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
    private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

    public void getTripById(){
        disposableLinkedList.add(TripApi.getTripByTripId(tripId).subscribe(trip -> {
            tripLive.postValue(trip);
        }, error -> {
            Timber.e(error);
            errorLiveMsg.postValue(error.getLocalizedMessage());
        }));
    }

    public BehaviorSubject<Boolean> deleteTripById() {
        return TripApi.deleteTripById(tripId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCleared() {
        super.onCleared();
        disposableLinkedList.forEach(Disposable::dispose);
    }
}
