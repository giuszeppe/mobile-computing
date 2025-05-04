package com.example.expensestracker.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import com.example.expensestracker.model.Category;
import com.example.expensestracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "expenses.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public interface CategoryChangeListener {
        void onCategoryChanged();
    }

    private static final List<CategoryChangeListener> categoryChangeListeners = new ArrayList<>();

    public static void registerCategoryChangeListener(CategoryChangeListener listener) {
        categoryChangeListeners.add(listener);
    }

    public static void notifyCategoryChangeListeners() {
        for (CategoryChangeListener listener : categoryChangeListeners) {
            listener.onCategoryChanged();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createExpensesTable = "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME + " (" +
                ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + " TEXT, " +
                ExpenseContract.ExpenseEntry.COLUMN_COST + " TEXT, " +
                ExpenseContract.ExpenseEntry.COLUMN_DATE + " TEXT)";

        String createCategoriesTable = "CREATE TABLE categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE, " +
                "colorHex TEXT)";

        db.execSQL(createExpensesTable);
        db.execSQL(createCategoriesTable);

        insertInitialCategory(db, "Food", "#FF5722");
        insertInitialCategory(db, "Transport", "#3F51B5");
        insertInitialCategory(db, "Other", "#9E9E9E");
    }

    private void insertInitialCategory(SQLiteDatabase db, String name, String colorHex) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("colorHex", colorHex);
        db.insert("categories", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS categories");
        onCreate(db);
    }

    public String getCategoryColor(String categoryName) {
        String colorHex = "#CCCCCC"; // default
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(
                "categories",
                new String[]{"colorHex"},
                "name = ?",
                new String[]{categoryName},
                null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
            }
        }

        return colorHex;
    }

    public List<String> getAllCategoryNames() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT name FROM categories", null)) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(0));
            }
        }

        return categories;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query("categories", null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String colorHex = cursor.getString(cursor.getColumnIndexOrThrow("colorHex"));
                categories.add(new Category(id, name, colorHex));
            }
        }

        return categories;
    }

    public void insertCategory(String name, String colorHex) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("colorHex", colorHex);
        getWritableDatabase().insert("categories", null, values);
        notifyCategoryChangeListeners();
    }

    public void updateCategory(Category category, String oldName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("colorHex", category.getColorHex());

        db.update("categories", values, "id=?", new String[]{String.valueOf(category.getId())});

        // Update related expenses
        ContentValues expenseUpdate = new ContentValues();
        expenseUpdate.put("category", category.getName());
        db.update("expenses", expenseUpdate, "category=?", new String[]{oldName});

        notifyCategoryChangeListeners();
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Reassign related expenses to "Other"
        ContentValues update = new ContentValues();
        update.put("category", "Other");
        db.update("expenses", update, "category = ?", new String[]{category.getName()});

        // Delete the category
        db.delete("categories", "id=?", new String[]{String.valueOf(category.getId())});

        notifyCategoryChangeListeners();
    }

    public long insertExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION, expense.getDescription());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY, expense.getCategory());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_COST, expense.getCost());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DATE, expense.getDate());

        return getWritableDatabase().insert(ExpenseContract.ExpenseEntry.TABLE_NAME, null, values);
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                ExpenseContract.ExpenseEntry.TABLE_NAME,
                null, null, null, null, null,
                ExpenseContract.ExpenseEntry.COLUMN_DATE + " DESC")) {

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry._ID));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION));
                String cat = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY));
                String cost = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_COST));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_DATE));
                expenses.add(new Expense(id, desc, cat, cost, date));
            }
        }

        return expenses;
    }

    public void updateExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION, expense.getDescription());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY, expense.getCategory());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_COST, expense.getCost());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DATE, expense.getDate());

        getWritableDatabase().update(
                ExpenseContract.ExpenseEntry.TABLE_NAME,
                values,
                ExpenseContract.ExpenseEntry._ID + " = ?",
                new String[]{String.valueOf(expense.getId())}
        );
    }

    public void deleteExpense(long id) {
        getWritableDatabase().delete(
                ExpenseContract.ExpenseEntry.TABLE_NAME,
                ExpenseContract.ExpenseEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }
}