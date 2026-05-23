package com.example.wardrobeplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wardrobeplanner.database.DatabaseHelper;
import com.example.wardrobeplanner.databinding.ActivityClothingListBinding;
import com.example.wardrobeplanner.models.ClothingItem;
import com.example.wardrobeplanner.ui.home.ClothingAdapter;

import java.util.List;

public class ClothingListActivity extends AppCompatActivity {

    private ActivityClothingListBinding binding;
    private DatabaseHelper databaseHelper;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClothingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClothingItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setupRecyclerView() {
        adapter = new ClothingAdapter();
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(this, ClothingDetailsActivity.class);
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_ID, item.getId());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_NAME, item.getName());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_CATEGORY, item.getCategory());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_COLOR, item.getColor());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_SEASON, item.getSeason());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_DESCRIPTION, item.getDescription());
            intent.putExtra(ClothingDetailsActivity.EXTRA_CLOTHING_IMAGE_URI, item.getImageUri());
            startActivity(intent);
        });

        binding.recyclerClothing.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerClothing.setAdapter(adapter);
    }

    private void loadClothingItems() {
        List<ClothingItem> items = databaseHelper.getAllClothes();
        adapter.setItems(items);
        boolean isEmpty = items == null || items.isEmpty();
        binding.recyclerClothing.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.textEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
