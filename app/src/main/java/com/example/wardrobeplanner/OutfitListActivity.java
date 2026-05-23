package com.example.wardrobeplanner;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityOutfitListBinding;
import com.example.wardrobeplanner.models.Outfit;
import com.example.wardrobeplanner.ui.outfits.OutfitAdapter;

import java.util.ArrayList;
import java.util.List;

public class OutfitListActivity extends AppCompatActivity {

    private ActivityOutfitListBinding binding;
    private DatabaseHelper databaseHelper;
    private OutfitAdapter outfitAdapter;
    private final List<Outfit> outfits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutfitListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        outfitAdapter = new OutfitAdapter(
                outfits,
                outfit -> {
                    Intent intent = new Intent(this, CreateOutfitActivity.class);
                    intent.putExtra(CreateOutfitActivity.EXTRA_EDIT_MODE, true);
                    intent.putExtra(CreateOutfitActivity.EXTRA_OUTFIT_ID, outfit.getId());
                    startActivity(intent);
                },
                outfit -> {
                    databaseHelper.deleteOutfit(outfit.getId());
                    Toast.makeText(this, "Outfit deleted", Toast.LENGTH_SHORT).show();
                    loadOutfits();
                }
        );

        binding.recyclerOutfits.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerOutfits.setAdapter(outfitAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOutfits();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void loadOutfits() {
        outfits.clear();
        outfits.addAll(databaseHelper.getAllOutfits());
        outfitAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = outfits.isEmpty();
        binding.textEmptyOutfits.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.recyclerOutfits.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
