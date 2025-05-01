package com.example.expensestracker.backend;

import android.provider.BaseColumns;

public final class ExpenseContract {
    private ExpenseContract() {}

    public static class ExpenseEntry implements BaseColumns {
        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_COST = "cost";
        public static final String COLUMN_DATE = "date";
    }
}
