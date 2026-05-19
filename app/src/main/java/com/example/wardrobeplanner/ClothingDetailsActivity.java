package com.example.wardrobeplanner;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityClothingDetailsBinding;

import java.io.File;

public class ClothingDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_CLOTHING_ID = "extra_clothing_id";
    public static final String EXTRA_CLOTHING_NAME = "extra_clothing_name";
    public static final String EXTRA_CLOTHING_CATEGORY = "extra_clothing_category";
    public static final String EXTRA_CLOTHING_COLOR = "extra_clothing_color";
    public static final String EXTRA_CLOTHING_SEASON = "extra_clothing_season";
    public static final String EXTRA_CLOTHING_DESCRIPTION = "extra_clothing_description";
    public static final String EXTRA_CLOTHING_IMAGE_URI = "extra_clothing_image_uri";

    private ActivityClothingDetailsBinding binding;
    private DatabaseHelper databaseHelper;
    private int clothingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClothingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        clothingId = getIntent().getIntExtra(EXTRA_CLOTHING_ID, -1);
        String name = getIntent().getStringExtra(EXTRA_CLOTHING_NAME);
        String category = getIntent().getStringExtra(EXTRA_CLOTHING_CATEGORY);
        String color = getIntent().getStringExtra(EXTRA_CLOTHING_COLOR);
        String season = getIntent().getStringExtra(EXTRA_CLOTHING_SEASON);
        String description = getIntent().getStringExtra(EXTRA_CLOTHING_DESCRIPTION);
        String imageUri = getIntent().getStringExtra(EXTRA_CLOTHING_IMAGE_URI);

        populateImage(imageUri);
        populateText(binding.textValueName, name);
        populateText(binding.textValueCategory, category);
        populateText(binding.textValueColor, color);
        populateText(binding.textValueSeason, season);
        populateText(binding.textValueDescription, description);

        binding.buttonDeleteClothing.setOnClickListener(v -> {
            if (clothingId != -1) {
                int rowsDeleted = databaseHelper.deleteClothing(clothingId);
                if (rowsDeleted > 0) {
                    Toast.makeText(this, "Clothing item deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to delete clothing item", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateImage(String imageUri) {
        if (imageUri != null && !imageUri.isEmpty() && !"no_path".equals(imageUri)) {
            try {
                File imageFile = new File(imageUri);
                if (imageFile.exists()) {
                    binding.imageClothingDetail.setImageURI(Uri.fromFile(imageFile));
                } else {
                    binding.imageClothingDetail.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } catch (Exception e) {
                binding.imageClothingDetail.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            binding.imageClothingDetail.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    private void populateText(android.widget.TextView textView, String value) {
        if (value != null && !value.isEmpty()) {
            textView.setText(value);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setText("—");
            textView.setVisibility(View.VISIBLE);
        }
    }
}