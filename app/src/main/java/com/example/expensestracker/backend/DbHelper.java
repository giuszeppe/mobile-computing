package com.example.expensestracker.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import com.example.expensestracker.Expense;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "expenses.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + ExpenseContract.ExpenseEntry.TABLE_NAME + " (" +
                ExpenseContract.ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION + " TEXT," +
                ExpenseContract.ExpenseEntry.COLUMN_CATEGORY + " TEXT," +
                ExpenseContract.ExpenseEntry.COLUMN_COST + " TEXT," +
                ExpenseContract.ExpenseEntry.COLUMN_DATE + " TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ExpenseContract.ExpenseEntry.TABLE_NAME);
        onCreate(db);
    }

    public long insertExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION, expense.getDescription());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY, expense.getCategory());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_COST, expense.getCost());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DATE, expense.getDate());
        return db.insert(ExpenseContract.ExpenseEntry.TABLE_NAME, null, values);
    }

    public List<Expense> getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Expense> expenses = new ArrayList<>();
        Cursor cursor = db.query(
                ExpenseContract.ExpenseEntry.TABLE_NAME,
                null, null, null, null, null,
                ExpenseContract.ExpenseEntry.COLUMN_DATE + " DESC"
        );
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry._ID));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION));
            String cat = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY));
            String cost = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_COST));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(ExpenseContract.ExpenseEntry.COLUMN_DATE));
            expenses.add(new Expense(id, desc, cat, cost, date));
        }
        cursor.close();
        return expenses;
    }

    public void updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DESCRIPTION, expense.getDescription());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_CATEGORY, expense.getCategory());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_COST, expense.getCost());
        values.put(ExpenseContract.ExpenseEntry.COLUMN_DATE, expense.getDate());

        String selection = ExpenseContract.ExpenseEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(expense.getId())};

        db.update(ExpenseContract.ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteExpense(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ExpenseContract.ExpenseEntry.TABLE_NAME,
                ExpenseContract.ExpenseEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}
