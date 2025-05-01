package com.example.expensestracker.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.Expense;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private final List<Expense> expenses;

    public ExpenseAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense, position);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, category, cost, date;
        Button editButton, deleteButton;
        Context context;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            description = itemView.findViewById(R.id.expense_description);
            category = itemView.findViewById(R.id.expense_category);
            cost = itemView.findViewById(R.id.expense_cost);
            date = itemView.findViewById(R.id.expense_date);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }

        public void bind(Expense expense, int position) {
            description.setText(expense.getDescription());
            category.setText("Category: " + expense.getCategory());
            cost.setText("€ " + expense.getCost());
            date.setText(expense.getDate());

            deleteButton.setOnClickListener(v -> {
                expenses.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });

            editButton.setOnClickListener(v -> showEditDialog(expense, position));
        }

        private void showEditDialog(Expense expense, int position) {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_expense, null);
            EditText editDesc = dialogView.findViewById(R.id.edit_description_dialog);
            EditText editCost = dialogView.findViewById(R.id.edit_cost_dialog);
            EditText editDate = dialogView.findViewById(R.id.edit_date_dialog);
            Spinner categorySpinner = dialogView.findViewById(R.id.spinner_category_dialog);

            editDesc.setText(expense.getDescription());
            editCost.setText(expense.getCost());
            editDate.setText(expense.getDate());

            ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                    context,
                    R.array.expense_categories,
                    android.R.layout.simple_spinner_item
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            int index = spinnerAdapter.getPosition(expense.getCategory());
            categorySpinner.setSelection(index);

            new AlertDialog.Builder(context)
                    .setTitle("Edit Expense")
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        expense.setDescription(editDesc.getText().toString());
                        expense.setCost(editCost.getText().toString());
                        expense.setDate(editDate.getText().toString());
                        expense.setCategory(categorySpinner.getSelectedItem().toString());
                        notifyItemChanged(position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
