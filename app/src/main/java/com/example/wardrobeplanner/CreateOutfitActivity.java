package com.example.wardrobeplanner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityCreateOutfitBinding;
import com.example.wardrobeplanner.models.ClothingItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
        updateFormState();
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

        sortItemsByName(topItems);
        sortItemsByName(bottomItems);
        sortItemsByName(shoesItems);

        binding.spinnerTop.setAdapter(createSpinnerAdapter(topItems, getString(R.string.select_top)));
        binding.spinnerBottom.setAdapter(createSpinnerAdapter(bottomItems, getString(R.string.select_bottom)));
        binding.spinnerShoes.setAdapter(createSpinnerAdapter(shoesItems, getString(R.string.select_shoes)));
        setupSpinnerListeners();
    }

    private void sortItemsByName(List<ClothingItem> items) {
        items.sort(Comparator
                .comparing((ClothingItem item) -> safeText(item.getName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(item -> safeText(item.getColor()), String.CASE_INSENSITIVE_ORDER));
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private ArrayAdapter<String> createSpinnerAdapter(List<ClothingItem> items, String selectionPrompt) {
        List<String> labels = new ArrayList<>();
        labels.add(selectionPrompt);
        for (ClothingItem item : items) {
            labels.add(item.getName() + " (" + item.getColor() + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void setupSaveButton() {
        binding.edittextOutfitName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateFormState();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed.
            }
        });

        binding.buttonSaveOutfit.setOnClickListener(v -> saveOutfit());
    }

    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFormState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateFormState();
            }
        };

        binding.spinnerTop.setOnItemSelectedListener(listener);
        binding.spinnerBottom.setOnItemSelectedListener(listener);
        binding.spinnerShoes.setOnItemSelectedListener(listener);
    }

    private void saveOutfit() {
        String outfitName = binding.edittextOutfitName.getText().toString().trim();

        if (outfitName.isEmpty()) {
            binding.edittextOutfitName.setError(getString(R.string.error_empty_outfit_name));
            updateFormState();
            return;
        }

        if (!hasRequiredCategories() || !hasSelectedAllCategories()) {
            updateFormState();
            return;
        }

        List<Integer> clothingIds = Arrays.asList(
                topItems.get(binding.spinnerTop.getSelectedItemPosition() - 1).getId(),
                bottomItems.get(binding.spinnerBottom.getSelectedItemPosition() - 1).getId(),
                shoesItems.get(binding.spinnerShoes.getSelectedItemPosition() - 1).getId()
        );

        long outfitId = databaseHelper.addOutfit(outfitName, clothingIds);
        if (outfitId == -1) {
            Toast.makeText(this, "Could not save outfit", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Outfit saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean hasSelectedAllCategories() {
        return binding.spinnerTop.getSelectedItemPosition() > 0
                && binding.spinnerBottom.getSelectedItemPosition() > 0
                && binding.spinnerShoes.getSelectedItemPosition() > 0;
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

    private void updateFormState() {
        StringBuilder message = new StringBuilder();
        String outfitName = binding.edittextOutfitName.getText().toString().trim();

        if (topItems.isEmpty()) {
            message.append(getString(R.string.error_missing_top));
        } else if (binding.spinnerTop.getSelectedItemPosition() == 0) {
            message.append(getString(R.string.error_select_top));
        }

        if (bottomItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_bottom));
        } else if (binding.spinnerBottom.getSelectedItemPosition() == 0) {
            appendMissingCategoryMessage(message, getString(R.string.error_select_bottom));
        }

        if (shoesItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_shoes));
        } else if (binding.spinnerShoes.getSelectedItemPosition() == 0) {
            appendMissingCategoryMessage(message, getString(R.string.error_select_shoes));
        }

        if (outfitName.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_empty_outfit_name));
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
