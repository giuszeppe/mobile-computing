package com.example.expensestracker.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensestracker.Expense;
import com.example.expensestracker.backend.DbHelper;
import com.example.expensestracker.databinding.FragmentListBinding;
import com.example.expensestracker.ui.ExpenseAdapter;

import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load from repository
        List<Expense> expenses = new DbHelper(requireContext()).getAllExpenses();

        ExpenseAdapter adapter = new ExpenseAdapter(expenses);
        binding.recyclerExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerExpenses.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
