package com.example.expensestracker.ui.profile;
import yuku.ambilwarna.AmbilWarnaDialog;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.expensestracker.R;
import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.model.Category;

import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout categoryListContainer;
    private DbHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = new DbHelper(requireContext());

        categoryListContainer = view.findViewById(R.id.category_list_container);
        Button addBtn = view.findViewById(R.id.add_category_btn);
        addBtn.setOnClickListener(v -> showAddDialog());

        refreshCategoryList();
        return view;
    }

    private void refreshCategoryList() {
        categoryListContainer.removeAllViews();
        List<Category> categories = db.getAllCategories();

        for (Category category : categories) {
            View row = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_category, categoryListContainer, false);

            TextView nameView = row.findViewById(R.id.category_name);
            View colorPreview = row.findViewById(R.id.color_preview);
            Button editBtn = row.findViewById(R.id.edit_btn);
            Button delBtn = row.findViewById(R.id.delete_btn);

            nameView.setText(category.getName());
            colorPreview.setBackgroundColor(Color.parseColor(category.getColorHex()));

            editBtn.setOnClickListener(v -> showEditDialog(category));
            delBtn.setOnClickListener(v -> {
                db.deleteCategory(category);
                refreshCategoryList();
            });

            categoryListContainer.addView(row);
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);
        EditText nameInput = dialogView.findViewById(R.id.input_name);
        View colorPreview = dialogView.findViewById(R.id.color_preview_box);
        Button pickColorBtn = dialogView.findViewById(R.id.color_picker_btn);

        final int[] selectedColor = {Color.RED}; // default

        pickColorBtn.setOnClickListener(v -> {
            AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(requireContext(), selectedColor[0], true,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            selectedColor[0] = color;
                            colorPreview.setBackgroundColor(color);
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }
                    });
            colorDialog.show();
        });

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Category")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString();
                    String hexColor = String.format("#%06X", (0xFFFFFF & selectedColor[0]));
                    db.insertCategory(name, hexColor);
                    refreshCategoryList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDialog(Category category) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_category, null);
        EditText nameInput = dialogView.findViewById(R.id.input_name);
        View colorPreview = dialogView.findViewById(R.id.color_preview_box);
        Button pickColorBtn = dialogView.findViewById(R.id.color_picker_btn);

        nameInput.setText(category.getName());
        int initialColor = Color.parseColor(category.getColorHex());
        final int[] selectedColor = {initialColor};
        colorPreview.setBackgroundColor(initialColor);

        pickColorBtn.setOnClickListener(v -> {
            AmbilWarnaDialog colorDialog = new AmbilWarnaDialog(requireContext(), selectedColor[0], true,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            selectedColor[0] = color;
                            colorPreview.setBackgroundColor(color);
                        }

                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {
                        }
                    });
            colorDialog.show();
        });

        String oldName = category.getName();

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Category")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    category.setName(nameInput.getText().toString());
                    category.setColorHex(String.format("#%06X", (0xFFFFFF & selectedColor[0])));
                    db.updateCategory(category, oldName);
                    refreshCategoryList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
