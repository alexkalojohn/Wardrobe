package com.example.wardrobeplanner.ui.outfits;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardrobeplanner.databinding.ItemOutfitBinding;
import com.example.wardrobeplanner.models.ClothingItem;
import com.example.wardrobeplanner.models.Outfit;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Outfit outfit);
    }

    private final List<Outfit> outfits;
    private final OnDeleteClickListener deleteClickListener;

    public OutfitAdapter(List<Outfit> outfits, OnDeleteClickListener deleteClickListener) {
        this.outfits = outfits;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOutfitBinding binding = ItemOutfitBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new OutfitViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        Outfit outfit = outfits.get(position);
        holder.bind(outfit, deleteClickListener);
    }

    @Override
    public int getItemCount() {
        return outfits.size();
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        private final ItemOutfitBinding binding;

        OutfitViewHolder(ItemOutfitBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Outfit outfit, OnDeleteClickListener deleteClickListener) {
            binding.textOutfitName.setText(outfit.getOutfitName());
            binding.textOutfitItems.setText(buildItemsText(outfit.getItems()));
            binding.buttonDeleteOutfit.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(outfit);
                }
            });
        }

        private String buildItemsText(List<ClothingItem> items) {
            if (items == null || items.isEmpty()) {
                binding.textOutfitItems.setVisibility(View.GONE);
                return "";
            }

            binding.textOutfitItems.setVisibility(View.VISIBLE);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < items.size(); i++) {
                ClothingItem item = items.get(i);
                builder.append(item.getName())
                        .append(" - ")
                        .append(item.getCategory());

                if (i < items.size() - 1) {
                    builder.append("\n");
                }
            }

            return builder.toString();
        }
    }
}
