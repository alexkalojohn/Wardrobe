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
import com.example.wardrobeplanner.databinding.FragmentHomeBinding;
import com.example.wardrobeplanner.models.ClothingItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ClothingAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupButtons();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        adapter = new ClothingAdapter();
        adapter.setItems(getMockClothingItems());
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

    private List<ClothingItem> getMockClothingItems() {
        List<ClothingItem> items = new ArrayList<>();
        items.add(new ClothingItem(1, "Blue Denim Jacket", "Jacket", "no_path", "Autumn", "Blue", "Classic denim jacket for casual wear"));
        items.add(new ClothingItem(2, "White Cotton T-Shirt", "Top", "no_path", "Summer", "White", "Breathable cotton tee"));
        items.add(new ClothingItem(3, "Black Slim Chinos", "Bottom", "no_path", "Spring", "Black", "Versatile slim-fit chinos"));
        items.add(new ClothingItem(4, "Leather Winter Boots", "Shoes", "no_path", "Winter", "Brown", "Waterproof leather boots"));
        items.add(new ClothingItem(5, "Red Wool Sweater", "Top", "no_path", "Winter", "Red", "Cozy wool knit sweater"));
        items.add(new ClothingItem(6, "Beige Linen Shorts", "Bottom", "no_path", "Summer", "Beige", "Lightweight linen shorts"));
        return items;
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