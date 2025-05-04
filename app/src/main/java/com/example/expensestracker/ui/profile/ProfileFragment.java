package com.example.expensestracker.ui.profile;

import yuku.ambilwarna.AmbilWarnaDialog;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensestracker.R;
import com.example.expensestracker.model.Category;

import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout categoryListContainer;

    private ProfileViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(ProfileViewModel.class);

        categoryListContainer = view.findViewById(R.id.category_list_container);

        Button addBtn = view.findViewById(R.id.add_category_btn);
        addBtn.setOnClickListener(v -> showAddDialog());

        // Observe the LiveData for category list updates
        viewModel.getCategories().observe(getViewLifecycleOwner(), this::populateCategoryList);

        return view;
    }

    private void populateCategoryList(List<Category> categories) {
        categoryListContainer.removeAllViews();

        for (Category category : categories) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_category, categoryListContainer, false);

            TextView nameView = row.findViewById(R.id.category_name);
            View colorPreview = row.findViewById(R.id.color_preview);
            Button editBtn = row.findViewById(R.id.edit_btn);
            Button delBtn = row.findViewById(R.id.delete_btn);

            // Set the name and color preview
            nameView.setText(category.getName());
            colorPreview.setBackgroundColor(Color.parseColor(category.getColorHex()));

            // Disable edit/delete for the "Other" category
            if (category.getName().equalsIgnoreCase("Other")) {
                delBtn.setEnabled(false);
                delBtn.setAlpha(0.5f);
                delBtn.setClickable(false);
                editBtn.setEnabled(false);
                editBtn.setAlpha(0.5f);
            } else {
                // Enable delete/edit for other categories
                delBtn.setOnClickListener(v -> viewModel.deleteCategory(category));
                editBtn.setOnClickListener(v -> showEditDialog(category));
            }

            categoryListContainer.addView(row);
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);
        EditText nameInput = dialogView.findViewById(R.id.input_name);
        View colorPreview = dialogView.findViewById(R.id.color_preview_box);
        Button pickColorBtn = dialogView.findViewById(R.id.color_picker_btn);

        final int[] selectedColor = {Color.RED};
        colorPreview.setBackgroundColor(selectedColor[0]);

        pickColorBtn.setOnClickListener(v -> {
            AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(requireContext(), selectedColor[0], true,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            selectedColor[0] = color;
                            colorPreview.setBackgroundColor(color);
                        }
                        public void onCancel(AmbilWarnaDialog dialog) {}
                    });
            colorDialog.show();
        });

        // Create and show add category dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Add Category")
                .setView(dialogView)
                .setPositiveButton("Add", null) // Add later with custom logic
                .setNegativeButton("Cancel", null)
                .create();

        // Enable "Add" button only when name is not empty
        dialog.setOnShowListener(dlg -> {
            Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            addButton.setEnabled(false);

            nameInput.addTextChangedListener(new android.text.TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    addButton.setEnabled(s.toString().trim().length() > 0);
                }
                public void afterTextChanged(android.text.Editable s) {}
            });

            addButton.setOnClickListener(v -> {
                String name = nameInput.getText().toString().trim();
                String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor[0]));
                viewModel.addCategory(name, hexColor);
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    // Show dialog to edit an existing category
    private void showEditDialog(Category category) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);
        EditText nameInput = dialogView.findViewById(R.id.input_name);
        View colorPreview = dialogView.findViewById(R.id.color_preview_box);
        Button pickColorBtn = dialogView.findViewById(R.id.color_picker_btn);

        // Pre-fill existing category data
        nameInput.setText(category.getName());
        final int[] selectedColor = {Color.parseColor(category.getColorHex())};
        colorPreview.setBackgroundColor(selectedColor[0]);

        // Launch color picker
        pickColorBtn.setOnClickListener(v -> {
            AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(requireContext(), selectedColor[0], true,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            selectedColor[0] = color;
                            colorPreview.setBackgroundColor(color);
                        }
                        public void onCancel(AmbilWarnaDialog dialog) {}
                    });
            colorDialog.show();
        });

        String oldName = category.getName();

        new AlertDialog.Builder(requireContext())
            .setTitle("Edit Category")
            .setView(dialogView)
            .setPositiveButton("Save", (dialog, which) -> {
                // Update category with new name and color
                category.setName(nameInput.getText().toString().trim());
                category.setColorHex(String.format("#%06X", (0xFFFFFF & selectedColor[0])));
                viewModel.updateCategory(category, oldName);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
