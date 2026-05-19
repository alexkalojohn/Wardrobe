package com.example.wardrobeplanner;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityAddClothingBinding;
import com.example.wardrobeplanner.models.ClothingItem;

public class AddClothingActivity extends AppCompatActivity {

    private ActivityAddClothingBinding binding;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;
    private String selectedSeason;
    private String selectedImageUri = "";

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.PickVisualMedia(),
                    uri -> {
                        if (uri != null) {
                            binding.imageClothingPreview.setImageURI(uri);
                            selectedImageUri = uri.toString();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClothingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        setupCategorySpinner();
        setupSeasonSpinner();
        setupButtonListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupCategorySpinner() {
        String[] categories = {"Top", "Bottom", "Shoes", "Jacket"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });
        selectedCategory = categories[0];
    }

    private void setupSeasonSpinner() {
        String[] seasons = {"Summer", "Winter", "Spring", "Autumn"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                seasons
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSeason.setAdapter(adapter);
        binding.spinnerSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSeason = seasons[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSeason = null;
            }
        });
        selectedSeason = seasons[0];
    }

    private void setupButtonListeners() {
        binding.buttonSelectImage.setOnClickListener(v -> {
            pickImageLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build()
            );
        });

        binding.buttonSaveClothing.setOnClickListener(v -> {
            boolean isValid = true;

            String name = binding.edittextClothingName.getText().toString().trim();
            String color = binding.edittextColor.getText().toString().trim();
            String description = binding.edittextDescription.getText().toString().trim();

            if (name.isEmpty()) {
                binding.edittextClothingName.setError("Name cannot be empty");
                isValid = false;
            } else {
                binding.edittextClothingName.setError(null);
            }

            if (color.isEmpty()) {
                binding.edittextColor.setError("Color cannot be empty");
                isValid = false;
            } else {
                binding.edittextColor.setError(null);
            }

            if (!isValid) {
                return;
            }

            ClothingItem clothingItem = new ClothingItem(
                    0,
                    name,
                    selectedCategory,
                    selectedImageUri,
                    selectedSeason,
                    color,
                    description
            );

            boolean success = databaseHelper.insertClothing(clothingItem);

            if (success) {
                android.widget.Toast.makeText(this, "Clothing item saved", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            } else {
                android.widget.Toast.makeText(this, "Failed to save clothing item", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}