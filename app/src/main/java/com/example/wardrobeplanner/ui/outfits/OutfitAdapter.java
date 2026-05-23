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

    public interface OnEditClickListener {
        void onEditClick(Outfit outfit);
    }

    private final List<Outfit> outfits;
    private final OnDeleteClickListener deleteClickListener;
    private final OnEditClickListener editClickListener;

    public OutfitAdapter(
            List<Outfit> outfits,
            OnEditClickListener editClickListener,
            OnDeleteClickListener deleteClickListener
    ) {
        this.outfits = outfits;
        this.editClickListener = editClickListener;
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
        holder.bind(outfit, editClickListener, deleteClickListener);
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

        void bind(
                Outfit outfit,
                OnEditClickListener editClickListener,
                OnDeleteClickListener deleteClickListener
        ) {
            binding.textOutfitName.setText(outfit.getOutfitName());
            bindItems(outfit.getItems());
            binding.buttonEditOutfit.setOnClickListener(v -> {
                if (editClickListener != null) {
                    editClickListener.onEditClick(outfit);
                }
            });
            binding.buttonDeleteOutfit.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(outfit);
                }
            });
        }

        private void bindItems(List<ClothingItem> items) {
            ClothingItem top = findItemByCategory(items, "Top");
            ClothingItem bottom = findItemByCategory(items, "Bottom");
            ClothingItem shoes = findItemByCategory(items, "Shoes");

            binding.textOutfitTop.setText("Top: " + getDisplayName(top));
            binding.textOutfitBottom.setText("Bottom: " + getDisplayName(bottom));
            binding.textOutfitShoes.setText("Shoes: " + getDisplayName(shoes));
        }

        private ClothingItem findItemByCategory(List<ClothingItem> items, String category) {
            if (items == null) {
                return null;
            }

            for (ClothingItem item : items) {
                if (category.equalsIgnoreCase(item.getCategory())) {
                    return item;
                }
            }
            return null;
        }

        private String getDisplayName(ClothingItem item) {
            if (item == null) {
                return "Deleted item";
            }

            return item.getName();
        }
    }
}
