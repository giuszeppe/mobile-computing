package com.example.expensestracker.ui.charts;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensestracker.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.*;

import java.util.*;

public class ChartsFragment extends Fragment {
    private TextView textRange;
    private PieChart pieChart;
    private final Calendar startDate = Calendar.getInstance();
    private final Calendar endDate = Calendar.getInstance();
    private ChartsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        textRange = view.findViewById(R.id.text_selected_range);
        pieChart = view.findViewById(R.id.pie_chart);
        Button buttonPickDates = view.findViewById(R.id.button_pick_dates);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(ChartsViewModel.class);

        observeViewModel();

        buttonPickDates.setOnClickListener(v -> pickDateRange());

        return view;
    }

    // Opens two date pickers to select start and end dates
    private void pickDateRange() {
        DatePickerDialog startDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            // Set start date
            startDate.set(year, month, dayOfMonth);

            // Show end date picker after start date is picked
            DatePickerDialog endDialog = new DatePickerDialog(requireContext(), (view2, year2, month2, day2) -> {
                endDate.set(year2, month2, day2);
                // Notify ViewModel of the selected date range
                viewModel.setDateRange(startDate, endDate);
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

            endDialog.setTitle("Select End Date");
            endDialog.show();

        }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));

        startDialog.setTitle("Select Start Date");
        startDialog.show();
    }

    private void observeViewModel() {
        // Observe chart entries
        viewModel.getChartEntries().observe(getViewLifecycleOwner(), entries -> {
            PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
            dataSet.setValueTextSize(14f);

            // Observe colors for the pie chart and apply them
            viewModel.getChartColors().observe(getViewLifecycleOwner(), colors -> {
                dataSet.setColors(colors);
                PieData pieData = new PieData(dataSet);
                pieChart.setData(pieData);
                pieChart.setUsePercentValues(true);

                // Remove chart description
                Description desc = new Description();
                desc.setText("");
                pieChart.setDescription(desc);

                pieChart.invalidate();
            });
        });

        // Observe label for the selected date range
        viewModel.getDateRangeLabel().observe(getViewLifecycleOwner(), label -> {
            textRange.setText(label);
        });
    }
}
