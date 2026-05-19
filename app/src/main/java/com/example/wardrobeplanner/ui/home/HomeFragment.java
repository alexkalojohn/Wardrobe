package com.example.wardrobeplanner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wardrobeplanner.AddClothingActivity;
import com.example.wardrobeplanner.ClothingDetailsActivity;
import com.example.wardrobeplanner.CreateOutfitActivity;
import com.example.wardrobeplanner.OutfitListActivity;
import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.FragmentHomeBinding;
import com.example.wardrobeplanner.models.ClothingItem;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ClothingAdapter adapter;
    private DatabaseHelper databaseHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseHelper = new DatabaseHelper(requireContext());

        setupRecyclerView();
        setupButtons();
        loadClothingItems();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClothingItems();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        adapter = new ClothingAdapter();
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(requireContext(), ClothingDetailsActivity.class);
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_ID, item.getId());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_NAME, item.getName());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_CATEGORY, item.getCategory());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_COLOR, item.getColor());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_SEASON, item.getSeason());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_DESCRIPTION, item.getDescription());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_IMAGE_URI, item.getImageUri());
            startActivity(intent);
        });
        binding.recyclerClothing.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerClothing.setAdapter(adapter);
    }

    private void loadClothingItems() {
        List<ClothingItem> items = databaseHelper.getAllClothes();
        adapter.setItems(items);
        updateEmptyState(items);
    }

    private void updateEmptyState(List<ClothingItem> items) {
        if (items == null || items.isEmpty()) {
            binding.recyclerClothing.setVisibility(View.GONE);
            binding.textEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerClothing.setVisibility(View.VISIBLE);
            binding.textEmptyState.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        binding.buttonAddClothing.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddClothingActivity.class);
            startActivity(intent);
        });

        binding.buttonCreateOutfit.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateOutfitActivity.class);
            startActivity(intent);
        });

        binding.buttonViewOutfits.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OutfitListActivity.class);
            startActivity(intent);
        });
    }
}