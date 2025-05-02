package com.example.expensestracker.ui.profile;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.model.Category;

import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final DbHelper dbHelper;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DbHelper(application);
        loadCategories();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void loadCategories() {
        categories.setValue(dbHelper.getAllCategories());
    }

    public void addCategory(String name, String hexColor) {
        dbHelper.insertCategory(name, hexColor);
        loadCategories();
    }

    public void deleteCategory(Category category) {
        dbHelper.deleteCategory(category);
        loadCategories();
    }

    public void updateCategory(Category newCategory, String oldName) {
        dbHelper.updateCategory(newCategory, oldName);
        loadCategories();
    }
}
