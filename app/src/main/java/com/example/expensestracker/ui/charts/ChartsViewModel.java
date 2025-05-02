package com.example.expensestracker.ui.charts;

import android.app.Application;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.model.Category;
import com.example.expensestracker.model.Expense;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChartsViewModel extends AndroidViewModel {

    private final DbHelper dbHelper;
    private final MutableLiveData<List<PieEntry>> chartEntries = new MutableLiveData<>();
    private final MutableLiveData<List<Integer>> chartColors = new MutableLiveData<>();
    private final MutableLiveData<String> dateRangeLabel = new MutableLiveData<>();

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Calendar startDate = null;
    private Calendar endDate = null;

    public ChartsViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DbHelper(application);
    }

    public LiveData<List<PieEntry>> getChartEntries() {
        return chartEntries;
    }

    public LiveData<List<Integer>> getChartColors() {
        return chartColors;
    }

    public LiveData<String> getDateRangeLabel() {
        return dateRangeLabel;
    }

    public void setDateRange(Calendar start, Calendar end) {
        this.startDate = start;
        this.endDate = end;
        updateChartData();
    }

    private void updateChartData() {
        if (startDate == null || endDate == null) return;

        String start = sdf.format(startDate.getTime());
        String end = sdf.format(endDate.getTime());
        dateRangeLabel.setValue("Showing from " + start + " to " + end);

        List<Expense> allExpenses = dbHelper.getAllExpenses();
        List<Category> allCategories = dbHelper.getAllCategories();

        Map<String, Float> categorySums = new HashMap<>();
        Map<String, String> categoryColorMap = new HashMap<>();

        for (Category c : allCategories) {
            categoryColorMap.put(c.getName(), c.getColorHex());
        }

        for (Expense e : allExpenses) {
            String date = e.getDate();
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                float amount = Float.parseFloat(e.getCost());
                categorySums.put(e.getCategory(),
                        categorySums.getOrDefault(e.getCategory(), 0f) + amount);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Float> entry : categorySums.entrySet()) {
            String cat = entry.getKey();
            entries.add(new PieEntry(entry.getValue(), cat));
            colors.add(Color.parseColor(categoryColorMap.getOrDefault(cat, "#9E9E9E")));
        }

        chartEntries.setValue(entries);
        chartColors.setValue(colors);
    }
}
