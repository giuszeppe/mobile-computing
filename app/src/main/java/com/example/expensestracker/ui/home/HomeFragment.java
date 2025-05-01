package com.example.expensestracker.ui.home;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensestracker.R;
import com.example.expensestracker.databinding.FragmentHomeBinding;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup Spinner
        Spinner categorySpinner = binding.spinnerCategory;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Setup Date Picker
        binding.editDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(requireContext(), (view1, y, m, d) -> {
                String selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d);
                binding.editDate.setText(selectedDate);
                checkFormValidity();
            }, year, month, day).show();
        });

        // a watcher for the two input fields
        binding.editDescription.addTextChangedListener(new SimpleWatcher(this::checkFormValidity));
        binding.editCost.addTextChangedListener(new SimpleWatcher(this::checkFormValidity));
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                checkFormValidity();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // save and reset form
        binding.buttonSave.setOnClickListener(v -> {
            String description = binding.editDescription.getText().toString();
            String category = binding.spinnerCategory.getSelectedItem().toString();
            String cost = binding.editCost.getText().toString();
            String date = binding.editDate.getText().toString();

            String summary = "Saved expense: " + description + " cost: " + cost + " $";
            Toast.makeText(requireContext(), summary, Toast.LENGTH_LONG).show();

            // cancel everything from the form
            binding.editDescription.setText("0", TextView.BufferType.EDITABLE);
            binding.editDescription.getText().clear();
            binding.editCost.setText("0", TextView.BufferType.EDITABLE);
            binding.editCost.getText().clear();
            binding.editDate.setText("0", TextView.BufferType.EDITABLE);
            binding.editDate.getText().clear();

        });

        return root;
    }

    // if the form is not filled in completely, the button should be disabled
    private void checkFormValidity() {
        boolean isValid =
                !binding.editDescription.getText().toString().trim().isEmpty() &&
                        !binding.editCost.getText().toString().trim().isEmpty() &&
                        !binding.editDate.getText().toString().trim().isEmpty() &&
                        binding.spinnerCategory.getSelectedItemPosition() != AdapterView.INVALID_POSITION;

        binding.buttonSave.setEnabled(isValid);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Simple text watcher helper
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
