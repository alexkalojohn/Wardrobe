package com.example.wardrobeplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityCreateOutfitBinding;
import com.example.wardrobeplanner.models.ClothingItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateOutfitActivity extends AppCompatActivity {

    private ActivityCreateOutfitBinding binding;
    private DatabaseHelper databaseHelper;
    private List<ClothingItem> topItems = new ArrayList<>();
    private List<ClothingItem> bottomItems = new ArrayList<>();
    private List<ClothingItem> shoesItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateOutfitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        setupClothingSpinners();
        setupSaveButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupClothingSpinners() {
        List<ClothingItem> clothingItems = databaseHelper.getAllClothing();

        for (ClothingItem item : clothingItems) {
            String category = item.getCategory();
            if ("Top".equalsIgnoreCase(category)) {
                topItems.add(item);
            } else if ("Bottom".equalsIgnoreCase(category)) {
                bottomItems.add(item);
            } else if ("Shoes".equalsIgnoreCase(category)) {
                shoesItems.add(item);
            }
        }

        binding.spinnerTop.setAdapter(createSpinnerAdapter(topItems));
        binding.spinnerBottom.setAdapter(createSpinnerAdapter(bottomItems));
        binding.spinnerShoes.setAdapter(createSpinnerAdapter(shoesItems));
        updateRequirementMessage();
    }

    private ArrayAdapter<String> createSpinnerAdapter(List<ClothingItem> items) {
        List<String> labels = new ArrayList<>();
        for (ClothingItem item : items) {
            labels.add(item.getName() + " (" + item.getColor() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setupSaveButton() {
        binding.buttonSaveOutfit.setOnClickListener(v -> saveOutfit());
    }

    private void saveOutfit() {
        String outfitName = binding.edittextOutfitName.getText().toString().trim();

        if (outfitName.isEmpty()) {
            binding.edittextOutfitName.setError("Outfit name cannot be empty");
            return;
        }

        if (!hasRequiredCategories()) {
            return;
        }

        int topPosition = binding.spinnerTop.getSelectedItemPosition();
        int bottomPosition = binding.spinnerBottom.getSelectedItemPosition();
        int shoesPosition = binding.spinnerShoes.getSelectedItemPosition();

        if (topPosition < 0 || bottomPosition < 0 || shoesPosition < 0) {
            Toast.makeText(this, "Select all outfit items", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> clothingIds = Arrays.asList(
                topItems.get(topPosition).getId(),
                bottomItems.get(bottomPosition).getId(),
                shoesItems.get(shoesPosition).getId()
        );

        long outfitId = databaseHelper.addOutfit(outfitName, clothingIds);
        if (outfitId == -1) {
            Toast.makeText(this, "Could not save outfit", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Outfit saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean hasRequiredCategories() {
        if (topItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_missing_top), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (bottomItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_missing_bottom), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (shoesItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_missing_shoes), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateRequirementMessage() {
        StringBuilder message = new StringBuilder();

        if (topItems.isEmpty()) {
            message.append(getString(R.string.error_missing_top));
        }

        if (bottomItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_bottom));
        }

        if (shoesItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_shoes));
        }

        if (message.length() == 0) {
            binding.textCreateOutfitState.setVisibility(View.GONE);
            binding.buttonSaveOutfit.setEnabled(true);
        } else {
            binding.textCreateOutfitState.setText(message.toString());
            binding.textCreateOutfitState.setVisibility(View.VISIBLE);
            binding.buttonSaveOutfit.setEnabled(false);
        }
    }

    private void appendMissingCategoryMessage(StringBuilder message, String nextMessage) {
        if (message.length() > 0) {
            message.append("\n");
        }
        message.append(nextMessage);
    }
}
