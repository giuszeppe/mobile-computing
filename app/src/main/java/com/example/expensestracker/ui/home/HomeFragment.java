package com.example.expensestracker.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensestracker.databinding.FragmentHomeBinding;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupObservers();
        setupFormLogic();

        return root;
    }

    private void setupObservers() {
        viewModel.getCategoryNames().observe(getViewLifecycleOwner(), this::setupSpinner);

        viewModel.isFormValid().observe(getViewLifecycleOwner(), isValid ->
                binding.buttonSave.setEnabled(isValid));

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            clearForm();
        });
    }

    private void setupSpinner(List<String> categoryNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        int index = categoryNames.indexOf("Other");
        if (index != -1) {
            binding.spinnerCategory.setSelection(index);
        }
    }

    private void setupFormLogic() {
        binding.editDate.setOnClickListener(view -> showDatePicker());

        binding.editDescription.addTextChangedListener(new SimpleWatcher(this::validateForm));
        binding.editCost.addTextChangedListener(new SimpleWatcher(this::validateForm));
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                validateForm();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.buttonSave.setOnClickListener(v -> {
            String desc = binding.editDescription.getText().toString();
            String cost = binding.editCost.getText().toString();
            String date = binding.editDate.getText().toString();
            String category = binding.spinnerCategory.getSelectedItem().toString();
            viewModel.saveExpense(desc, category, cost, date);
        });
    }

    private void validateForm() {
        String desc = binding.editDescription.getText().toString();
        String cost = binding.editCost.getText().toString();
        String date = binding.editDate.getText().toString();
        int catPos = binding.spinnerCategory.getSelectedItemPosition();
        viewModel.validateForm(desc, cost, date, catPos);
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, y, m, d) -> {
            String selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
            binding.editDate.setText(selectedDate);
            validateForm();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void clearForm() {
        binding.editDescription.getText().clear();
        binding.editCost.getText().clear();
        binding.editDate.getText().clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class SimpleWatcher implements android.text.TextWatcher {
        private final Runnable onChange;
        SimpleWatcher(Runnable onChange) {
            this.onChange = onChange;
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChange.run();
        }
        public void afterTextChanged(android.text.Editable s) {}
    }
}
