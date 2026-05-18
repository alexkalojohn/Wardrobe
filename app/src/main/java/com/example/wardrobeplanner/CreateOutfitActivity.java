package com.example.wardrobeplanner;

import android.os.Bundle;
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
    private List<ClothingItem> clothingItems = new ArrayList<>();

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
        clothingItems = databaseHelper.getAllClothing();

        List<String> labels = new ArrayList<>();
        for (ClothingItem item : clothingItems) {
            labels.add(item.getName() + " (" + item.getCategory() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerTop.setAdapter(adapter);
        binding.spinnerBottom.setAdapter(adapter);
        binding.spinnerShoes.setAdapter(adapter);
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

        if (clothingItems.size() < 3) {
            Toast.makeText(this, "Add at least three clothing items first", Toast.LENGTH_SHORT).show();
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
                clothingItems.get(topPosition).getId(),
                clothingItems.get(bottomPosition).getId(),
                clothingItems.get(shoesPosition).getId()
        );

        long outfitId = databaseHelper.addOutfit(outfitName, clothingIds);
        if (outfitId == -1) {
            Toast.makeText(this, "Could not save outfit", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Outfit saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
