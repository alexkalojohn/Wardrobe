package com.example.wardrobeplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityCreateOutfitBinding;
import com.example.wardrobeplanner.models.ClothingItem;
import com.example.wardrobeplanner.models.Outfit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CreateOutfitActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_MODE = "extra_edit_mode";
    public static final String EXTRA_OUTFIT_ID = "extra_outfit_id";

    private ActivityCreateOutfitBinding binding;
    private DatabaseHelper databaseHelper;

    private static final String PREFS_NAME = "WardrobePrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "logged_in_user_id";
    private List<ClothingItem> topItems = new ArrayList<>();
    private List<ClothingItem> bottomItems = new ArrayList<>();
    private List<ClothingItem> shoesItems = new ArrayList<>();
    private int selectedTopIndex = -1;
    private int selectedBottomIndex = -1;
    private int selectedShoesIndex = -1;
    private boolean editMode;
    private int editingOutfitId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateOutfitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        editMode = getIntent().getBooleanExtra(EXTRA_EDIT_MODE, false);
        editingOutfitId = getIntent().getIntExtra(EXTRA_OUTFIT_ID, -1);

        setupBackNavigation();
        setupClothingSpinners();
        setupSelectionButtons();
        setupSaveButton();
        loadOutfitForEditing();
        updateFormState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(KEY_LOGGED_IN_USER_ID, -1);
    }

    private void setupClothingSpinners() {
        List<ClothingItem> clothingItems = databaseHelper.getAllClothing(getCurrentUserId());

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

        updateSelectionLabels();
    }

    private void sortItemsByName(List<ClothingItem> items) {
        items.sort(Comparator
                .comparing((ClothingItem item) -> safeText(item.getName()), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(item -> safeText(item.getColor()), String.CASE_INSENSITIVE_ORDER));
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private void setupBackNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    private void setupSelectionButtons() {
        binding.buttonSelectTop.setOnClickListener(v ->
                showSelectionDialog(getString(R.string.label_top), topItems, selectedTopIndex, index -> {
                    selectedTopIndex = index;
                    updateSelectionLabels();
                    updateFormState();
                })
        );

        binding.buttonSelectBottom.setOnClickListener(v ->
                showSelectionDialog(getString(R.string.label_bottom), bottomItems, selectedBottomIndex, index -> {
                    selectedBottomIndex = index;
                    updateSelectionLabels();
                    updateFormState();
                })
        );

        binding.buttonSelectShoes.setOnClickListener(v ->
                showSelectionDialog(getString(R.string.label_shoes), shoesItems, selectedShoesIndex, index -> {
                    selectedShoesIndex = index;
                    updateSelectionLabels();
                    updateFormState();
                })
        );
    }

    private interface SelectionCallback {
        void onSelected(int index);
    }

    private void showSelectionDialog(
            String title,
            List<ClothingItem> items,
            int selectedIndex,
            SelectionCallback callback
    ) {
        if (items.isEmpty()) {
            Toast.makeText(this, "No " + title + " items available", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] labels = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            labels[i] = getItemLabel(items.get(i));
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setSingleChoiceItems(labels, selectedIndex, (dialog, which) -> {
                    callback.onSelected(which);
                    dialog.dismiss();
                })
                .show();
    }

    private String getItemLabel(ClothingItem item) {
        String color = safeText(item.getColor());
        if (color.isEmpty()) {
            return item.getName();
        }
        return item.getName() + " (" + color + ")";
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
                topItems.get(selectedTopIndex).getId(),
                bottomItems.get(selectedBottomIndex).getId(),
                shoesItems.get(selectedShoesIndex).getId()
        );

        if (editMode) {
            boolean success = databaseHelper.updateOutfit(editingOutfitId, outfitName, clothingIds, getCurrentUserId());
            if (!success) {
                Toast.makeText(this, "Could not update outfit", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Outfit updated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        long outfitId = databaseHelper.addOutfit(outfitName, clothingIds, getCurrentUserId());
        if (outfitId == -1) {
            Toast.makeText(this, "Could not save outfit", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Outfit saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean hasSelectedAllCategories() {
        return selectedTopIndex >= 0
                && selectedBottomIndex >= 0
                && selectedShoesIndex >= 0;
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
        } else if (selectedTopIndex < 0) {
            message.append(getString(R.string.error_select_top));
        }

        if (bottomItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_bottom));
        } else if (selectedBottomIndex < 0) {
            appendMissingCategoryMessage(message, getString(R.string.error_select_bottom));
        }

        if (shoesItems.isEmpty()) {
            appendMissingCategoryMessage(message, getString(R.string.error_missing_shoes));
        } else if (selectedShoesIndex < 0) {
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

    private void loadOutfitForEditing() {
        if (!editMode || editingOutfitId == -1) {
            return;
        }

        Outfit outfit = databaseHelper.getOutfitById(editingOutfitId);
        if (outfit == null) {
            Toast.makeText(this, "Could not load outfit", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.edittextOutfitName.setText(outfit.getOutfitName());
        binding.buttonSaveOutfit.setText(R.string.button_save_outfit);

        for (ClothingItem item : outfit.getItems()) {
            if ("Top".equalsIgnoreCase(item.getCategory())) {
                selectedTopIndex = findItemIndex(topItems, item.getId());
            } else if ("Bottom".equalsIgnoreCase(item.getCategory())) {
                selectedBottomIndex = findItemIndex(bottomItems, item.getId());
            } else if ("Shoes".equalsIgnoreCase(item.getCategory())) {
                selectedShoesIndex = findItemIndex(shoesItems, item.getId());
            }
        }

        updateSelectionLabels();
    }

    private int findItemIndex(List<ClothingItem> items, int itemId) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    private void updateSelectionLabels() {
        binding.buttonSelectTop.setText(selectedTopIndex >= 0
                ? getItemLabel(topItems.get(selectedTopIndex))
                : getString(R.string.select_top));
        binding.buttonSelectBottom.setText(selectedBottomIndex >= 0
                ? getItemLabel(bottomItems.get(selectedBottomIndex))
                : getString(R.string.select_bottom));
        binding.buttonSelectShoes.setText(selectedShoesIndex >= 0
                ? getItemLabel(shoesItems.get(selectedShoesIndex))
                : getString(R.string.select_shoes));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void appendMissingCategoryMessage(StringBuilder message, String nextMessage) {
        if (message.length() > 0) {
            message.append("\n");
        }
        message.append(nextMessage);
    }
}
