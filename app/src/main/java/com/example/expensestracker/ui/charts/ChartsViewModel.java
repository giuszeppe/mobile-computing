package com.example.expensestracker.ui.charts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChartsViewModel extends ViewModel {
    private MutableLiveData<List<String>> items;
    public ChartsViewModel() {
        items = new MutableLiveData<>();
    }
    public MutableLiveData<List<String>> getItems() {
        ArrayList<String> notifications = new ArrayList<>();
        for(int i =0; i < 10; i++){
            notifications.add("Notification " + (i+1));
        }
        items.setValue(notifications);
        return items;
    }
}