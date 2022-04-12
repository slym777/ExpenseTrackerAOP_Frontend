package com.example.expensetracker.ui.notifications;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.expensetracker.api.UserApi;
import com.example.expensetracker.model.Notification;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

public class NotificationsViewModel extends ViewModel {
        public MutableLiveData<List<Notification>> notificationLiveList = new MutableLiveData<>();
        public MutableLiveData<String> errorLiveMsg = new MutableLiveData<>();
        private final LinkedList<Disposable> disposableLinkedList = new LinkedList<>();

        public void getAllNotifications(){
                disposableLinkedList.add(UserApi.getListOfNotifications().subscribe(
                        list -> notificationLiveList.postValue(list),
                        error -> {
                                Timber.e(error);
                                errorLiveMsg.postValue(error.getLocalizedMessage());
                        })
                );
        }
}
