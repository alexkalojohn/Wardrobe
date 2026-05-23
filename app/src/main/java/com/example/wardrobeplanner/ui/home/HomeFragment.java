package com.example.wardrobeplanner.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.wardrobeplanner.AddClothingActivity;
import com.example.wardrobeplanner.ClothingListActivity;
import com.example.wardrobeplanner.CreateOutfitActivity;
import com.example.wardrobeplanner.OutfitListActivity;
import com.example.wardrobeplanner.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupButtons();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupButtons() {
        binding.cardViewClothing.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ClothingListActivity.class);
            startActivity(intent);
        });

        binding.cardAddClothing.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddClothingActivity.class);
            startActivity(intent);
        });

        binding.cardCreateOutfit.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateOutfitActivity.class);
            startActivity(intent);
        });

        binding.cardViewOutfits.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OutfitListActivity.class);
            startActivity(intent);
        });
    }
}
