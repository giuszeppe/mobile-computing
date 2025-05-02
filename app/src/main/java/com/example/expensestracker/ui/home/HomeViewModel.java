package com.example.expensestracker.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.model.Category;
import com.example.expensestracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final DbHelper dbHelper;
    private final MutableLiveData<List<String>> categoryNames = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFormValid = new MutableLiveData<>(false);
    private final MutableLiveData<String> saveResult = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DbHelper(application);
        loadCategories();
    }

    public LiveData<List<String>> getCategoryNames() {
        return categoryNames;
    }

    public LiveData<Boolean> isFormValid() {
        return isFormValid;
    }

    public LiveData<String> getSaveResult() {
        return saveResult;
    }

    public void validateForm(String desc, String cost, String date, int catPos) {
        boolean valid = !desc.trim().isEmpty() && !cost.trim().isEmpty()
                && !date.trim().isEmpty() && catPos != -1;
        isFormValid.setValue(valid);
    }

    public void saveExpense(String description, String category, String cost, String date) {
        Expense expense = new Expense(-1, description, category, cost, date);
        dbHelper.insertExpense(expense);
        saveResult.setValue("Saved expense: " + description + " cost: " + cost + " $");
    }

    private void loadCategories() {
        List<Category> categories = dbHelper.getAllCategories();
        List<String> names = new ArrayList<>();
        for (Category c : categories) {
            names.add(c.getName());
        }
        categoryNames.setValue(names);
    }
}
