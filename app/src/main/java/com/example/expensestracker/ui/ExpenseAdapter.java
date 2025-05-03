package com.example.expensestracker.ui;

import com.google.android.material.card.MaterialCardView;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.model.Expense;
import com.example.expensestracker.backend.DbHelper;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private List<Expense> expenses;
    private Context context;
    private OnExpenseChangeListener listener;

    public ExpenseAdapter(Context context, List<Expense> expenses, OnExpenseChangeListener listener) {
        this.context = context;
        this.expenses = expenses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.bind(expense, position);
    }

    public void setExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView description, category, cost, date;
        Button editButton, deleteButton;
        MaterialCardView expenseCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.expense_description);
            category = itemView.findViewById(R.id.expense_category);
            cost = itemView.findViewById(R.id.expense_cost);
            date = itemView.findViewById(R.id.expense_date);
            editButton = itemView.findViewById(R.id.button_edit);
            deleteButton = itemView.findViewById(R.id.button_delete);
            expenseCard = itemView.findViewById(R.id.expense_card);
        }

        public void bind(Expense expense, int position) {
            description.setText(expense.getDescription());
            category.setText("Category: " + expense.getCategory());
            cost.setText("€ " + expense.getCost());
            date.setText(expense.getDate());

            deleteButton.setOnClickListener(v -> {
                new DbHelper(context).deleteExpense(expense.getId());
                expenses.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());

                if (listener != null) {
                    listener.onExpenseListChanged();
                }
            });

            DbHelper dbHelper = new DbHelper(context);
            String categoryName = expense.getCategory();
            String colorHex = dbHelper.getCategoryColor(categoryName); // You need to implement this

            try {
                int borderColor = android.graphics.Color.parseColor(colorHex);
                expenseCard.setStrokeColor(borderColor);
            } catch (IllegalArgumentException e) {
                // Fallback color if parse fails
                expenseCard.setStrokeColor(android.graphics.Color.GRAY);
            }


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

            DbHelper dbHelper = new DbHelper(context);
            List<String> categoryList = dbHelper.getAllCategoryNames();

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_spinner_item,
                    categoryList
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);

            // Pre-select the current category
            int index = categoryList.indexOf(expense.getCategory());
            if (index != -1) {
                categorySpinner.setSelection(index);
            }


            new AlertDialog.Builder(context)
                    .setTitle("Edit Expense")
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        expense.setDescription(editDesc.getText().toString());
                        expense.setCost(editCost.getText().toString());
                        expense.setDate(editDate.getText().toString());
                        expense.setCategory(categorySpinner.getSelectedItem().toString());

                        new DbHelper(context).updateExpense(expense);
                        notifyItemChanged(position);

                        if (listener != null) {
                            listener.onExpenseListChanged();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // 🔁 Callback interface to notify fragment/activity on changes
    public interface OnExpenseChangeListener {
        void onExpenseListChanged();
    }
}
