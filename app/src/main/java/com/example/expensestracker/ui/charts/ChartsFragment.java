package com.example.expensestracker.ui.charts;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.expensestracker.R;
import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.Expense;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChartsFragment extends Fragment {

    private TextView textRange;
    private PieChart pieChart;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        Button buttonPickDates = view.findViewById(R.id.button_pick_dates);
        textRange = view.findViewById(R.id.text_selected_range);
        pieChart = view.findViewById(R.id.pie_chart);

        buttonPickDates.setOnClickListener(v -> pickDateRange());

        return view;
    }

    private void pickDateRange() {
        DatePickerDialog startDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            startDate.set(year, month, dayOfMonth);

            DatePickerDialog endDialog = new DatePickerDialog(requireContext(), (view2, year2, month2, day2) -> {
                endDate.set(year2, month2, day2);
                updateChart();
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

            endDialog.setTitle("Select End Date");
            endDialog.show();

        }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));

        startDialog.setTitle("Select Start Date");
        startDialog.show();
    }

    private void updateChart() {
        String start = dateFormat.format(startDate.getTime());
        String end = dateFormat.format(endDate.getTime());
        textRange.setText("Showing from " + start + " to " + end);

        List<Expense> allExpenses = new DbHelper(requireContext()).getAllExpenses();
        Map<String, Float> categorySums = new HashMap<>();

        for (Expense exp : allExpenses) {
            String date = exp.getDate();
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) {
                float amount = Float.parseFloat(exp.getCost());
                categorySums.put(exp.getCategory(),
                        categorySums.getOrDefault(exp.getCategory(), 0f) + amount);
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categorySums.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(new int[]{
                R.color.purple_200,
                R.color.teal_200,
                R.color.purple_500,
                R.color.teal_700,
                R.color.black
        }, requireContext());
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setUsePercentValues(true);
        pieChart.invalidate(); // refresh chart
    }
}
