package com.example.expensestracker;

import com.example.expensestracker.model.Expense;
import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository {
    private static final ExpenseRepository instance = new ExpenseRepository();
    private final List<Expense> expenses = new ArrayList<>();

    private ExpenseRepository() {}

    public static ExpenseRepository getInstance() {
        return instance;
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }
}
