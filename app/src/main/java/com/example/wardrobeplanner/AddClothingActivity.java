package com.example.wardrobeplanner;

import android.content.SharedPreferences;
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

    public static final String EXTRA_EDIT_MODE = "extra_edit_mode";
    public static final String EXTRA_CLOTHING_ID = "extra_clothing_id";
    public static final String EXTRA_CLOTHING_NAME = "extra_clothing_name";
    public static final String EXTRA_CLOTHING_CATEGORY = "extra_clothing_category";
    public static final String EXTRA_CLOTHING_COLOR = "extra_clothing_color";
    public static final String EXTRA_CLOTHING_SEASON = "extra_clothing_season";
    public static final String EXTRA_CLOTHING_DESCRIPTION = "extra_clothing_description";
    public static final String EXTRA_CLOTHING_IMAGE_URI = "extra_clothing_image_uri";

    private ActivityAddClothingBinding binding;
    private DatabaseHelper databaseHelper;
    private String selectedCategory;
    private String selectedSeason;
    private String selectedImageUri = "";
    private boolean isEditMode = false;
    private int editClothingId = -1;

    private static final String PREFS_NAME = "WardrobePrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        isEditMode = getIntent().getBooleanExtra(EXTRA_EDIT_MODE, false);
        if (isEditMode) {
            editClothingId = getIntent().getIntExtra(EXTRA_CLOTHING_ID, -1);
        }

        setupCategorySpinner();
        setupSeasonSpinner();
        setupButtonListeners();

        if (isEditMode) {
            populateEditFields();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void populateEditFields() {
        binding.edittextClothingName.setText(getIntent().getStringExtra(EXTRA_CLOTHING_NAME));
        binding.edittextColor.setText(getIntent().getStringExtra(EXTRA_CLOTHING_COLOR));
        binding.edittextDescription.setText(getIntent().getStringExtra(EXTRA_CLOTHING_DESCRIPTION));

        selectedImageUri = getIntent().getStringExtra(EXTRA_CLOTHING_IMAGE_URI);
        if (selectedImageUri != null && !selectedImageUri.isEmpty() && !"no_path".equals(selectedImageUri)) {
            try {
                java.io.File imageFile = new java.io.File(selectedImageUri);
                if (imageFile.exists()) {
                    binding.imageClothingPreview.setImageURI(android.net.Uri.fromFile(imageFile));
                }
            } catch (Exception e) {
                // ignore
            }
        }

        String category = getIntent().getStringExtra(EXTRA_CLOTHING_CATEGORY);
        String season = getIntent().getStringExtra(EXTRA_CLOTHING_SEASON);

        if (category != null) {
            selectedCategory = category;
            android.widget.ArrayAdapter adapter = (android.widget.ArrayAdapter) binding.spinnerCategory.getAdapter();
            int position = adapter.getPosition(category);
            if (position >= 0) {
                binding.spinnerCategory.setSelection(position);
            }
        }

        if (season != null) {
            selectedSeason = season;
            android.widget.ArrayAdapter adapter = (android.widget.ArrayAdapter) binding.spinnerSeason.getAdapter();
            int position = adapter.getPosition(season);
            if (position >= 0) {
                binding.spinnerSeason.setSelection(position);
            }
        }

        binding.buttonSaveClothing.setText("Update Clothing");
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

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_LOGGED_IN_USER_ID, -1);
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

            int userId = getCurrentUserId();
            boolean success;
            if (isEditMode && editClothingId != -1) {
                ClothingItem clothingItem = new ClothingItem(
                        editClothingId,
                        name,
                        selectedCategory,
                        selectedImageUri,
                        selectedSeason,
                        color,
                        description
                );
                success = databaseHelper.updateClothing(clothingItem);
                if (success) {
                    android.widget.Toast.makeText(this, "Clothing item updated", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    android.widget.Toast.makeText(this, "Failed to update clothing item", android.widget.Toast.LENGTH_SHORT).show();
                }
            } else {
                ClothingItem clothingItem = new ClothingItem(
                        0,
                        name,
                        selectedCategory,
                        selectedImageUri,
                        selectedSeason,
                        color,
                        description,
                        userId
                );
                success = databaseHelper.insertClothing(clothingItem);
                if (success) {
                    android.widget.Toast.makeText(this, "Clothing item saved", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    android.widget.Toast.makeText(this, "Failed to save clothing item", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
