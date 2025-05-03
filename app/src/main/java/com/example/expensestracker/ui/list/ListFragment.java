package com.example.expensestracker.ui.list;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.databinding.FragmentListBinding;
import com.example.expensestracker.model.Expense;
import com.example.expensestracker.ui.ExpenseAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListFragment extends Fragment {

    private ListViewModel viewModel;
    private FragmentListBinding binding;
    private ExpenseAdapter adapter;

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    private TextView selectedRangeText;
    private TextView totalAmountText;
    private Spinner categorySpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        viewModel = new ViewModelProvider(this).get(ListViewModel.class);

        selectedRangeText = root.findViewById(R.id.selected_range);
        totalAmountText = root.findViewById(R.id.text_total_amount);
        categorySpinner = root.findViewById(R.id.filter_category_spinner);
        Button pickRangeBtn = root.findViewById(R.id.button_pick_range);

        setupCategorySpinner();
        setupRecyclerView(root);
        observeViewModel();

        pickRangeBtn.setOnClickListener(v -> showDatePickerDialog());

        return root;
    }

    private void setupCategorySpinner() {
        List<String> categories = viewModel.getAllCategoryNames();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories.get(position);
                viewModel.setCategory(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupRecyclerView(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter(requireContext(), List.of(), () -> {
            viewModel.reloadExpenses();
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getFilteredExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.setExpenses(expenses);
        });

        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), amount -> {
            totalAmountText.setText(String.format(Locale.getDefault(), "Total: €%.2f", amount));
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog startDialog = new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            startDate.set(year, month, day);

            DatePickerDialog endDialog = new DatePickerDialog(requireContext(), (view1, year1, month1, day1) -> {
                endDate.set(year1, month1, day1);
                viewModel.setDateRange(startDate, endDate);
                updateDateLabel();
            }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));

            endDialog.setTitle("Select End Date");
            endDialog.show();

        }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));

        startDialog.setTitle("Select Start Date");
        startDialog.show();
    }

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String startStr = sdf.format(startDate.getTime());
        String endStr = sdf.format(endDate.getTime());
        selectedRangeText.setText("From " + startStr + " to " + endStr);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDataFromDatabase();
    }

    private void refreshDataFromDatabase() {
        viewModel.reloadExpenses(); // you’ll implement this next in your ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
