package com.example.expensestracker.ui.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensestracker.R;
import com.example.expensestracker.databinding.FragmentChartsBinding;

import java.util.ArrayList;
import java.util.List;

public class ChartsFragment extends Fragment {
    private ChartsViewModel chartsViewModel;
    private @NonNull FragmentChartsBinding binding;
    private RecyclerView itemsView;
    private List<String> items = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup
            container, Bundle savedInstanceState) {
        chartsViewModel = new
                ViewModelProvider(this).get(ChartsViewModel.class);
        binding = FragmentChartsBinding.inflate(inflater, container,
                false);
        View root = binding.getRoot();
        itemsView = root.findViewById(R.id.items_list);
        setUpRecyclerView();
        fetchItems();
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void fetchItems() {
    }
    private void setUpRecyclerView() {
    }
    private void updateItemsList(List<String> newItems) {
    }
}