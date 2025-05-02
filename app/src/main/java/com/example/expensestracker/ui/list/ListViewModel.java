package com.example.expensestracker.ui.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.model.Expense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListViewModel extends AndroidViewModel {

    private final DbHelper db;
    private final List<Expense> allExpenses;

    private final MutableLiveData<List<Expense>> filteredExpenses = new MutableLiveData<>();
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>();

    private String selectedCategory = "All";
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private boolean isDateFilterActive = false;

    public ListViewModel(@NonNull Application application) {
        super(application);
        db = new DbHelper(application);
        allExpenses = db.getAllExpenses();
        applyFilters();
    }

    public LiveData<List<Expense>> getFilteredExpenses() {
        return filteredExpenses;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public void setCategory(String category) {
        selectedCategory = category;
        applyFilters();
    }

    public void setDateRange(Calendar start, Calendar end) {
        startDate = start;
        endDate = end;
        isDateFilterActive = true;
        applyFilters();
    }
    public void clearDateFilter() {
        isDateFilterActive = false;
        applyFilters();
    }

    private void applyFilters() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String start = sdf.format(startDate.getTime());
        String end = sdf.format(endDate.getTime());

        List<Expense> result = new ArrayList<>();
        double total = 0.0;

        for (Expense exp : allExpenses) {
            boolean matchesCategory = selectedCategory.equals("All") || exp.getCategory().equals(selectedCategory);
            boolean matchesDate = true;

            if (isDateFilterActive) {
                matchesDate = exp.getDate().compareTo(start) >= 0 && exp.getDate().compareTo(end) <= 0;
            }

            if (matchesCategory && matchesDate) {
                result.add(exp);
                total += Double.parseDouble(exp.getCost());
            }
        }

        filteredExpenses.setValue(result);
        totalAmount.setValue(total);
    }

    public List<String> getAllCategoryNames() {
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("All");
        db.getAllCategories().forEach(c -> categoryNames.add(c.getName()));
        return categoryNames;
    }
}
