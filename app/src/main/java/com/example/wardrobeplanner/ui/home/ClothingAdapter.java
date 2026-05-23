package com.example.wardrobeplanner.ui.home;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wardrobeplanner.R;
import com.example.wardrobeplanner.models.ClothingItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ClothingViewHolder> {

    private List<ClothingItem> clothingItems = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    /**
     * Interface for item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(ClothingItem item, int position);
    }

    /**
     * Set the click listener for items.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ClothingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_clothing, parent, false);
        return new ClothingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothingViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);
        holder.bind(item);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return clothingItems != null ? clothingItems.size() : 0;
    }

    /**
     * Replace the current list with a new one and refresh the view.
     */
    public void setItems(List<ClothingItem> items) {
        this.clothingItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Get the current item list.
     */
    public List<ClothingItem> getItems() {
        return new ArrayList<>(clothingItems);
    }

    static class ClothingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageClothing;
        private final ImageView imageCategoryIcon;
        private final TextView textName;
        private final TextView textCategory;
        private final TextView textSeason;

        ClothingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageClothing = itemView.findViewById(R.id.image_clothing);
            imageCategoryIcon = itemView.findViewById(R.id.image_category_icon);
            textName = itemView.findViewById(R.id.text_clothing_name);
            textCategory = itemView.findViewById(R.id.text_clothing_category);
            textSeason = itemView.findViewById(R.id.text_clothing_season);
        }

        void bind(ClothingItem item) {
            textName.setText(item.getName());
            textCategory.setText(item.getCategory());
            imageCategoryIcon.setImageResource(getCategoryIcon(item.getCategory()));

            // Show season if available, otherwise hide the label
            if (item.getSeason() != null && !item.getSeason().isEmpty()) {
                textSeason.setText(item.getSeason());
                textSeason.setVisibility(View.VISIBLE);
            } else {
                textSeason.setVisibility(View.GONE);
            }

            // Handle image: try to load from file URI, otherwise show placeholder
            String imageUri = item.getImageUri();
            if (imageUri != null && !imageUri.isEmpty() && !"no_path".equals(imageUri)) {
                try {
                    File imageFile = new File(imageUri);
                    if (imageFile.exists()) {
                        imageClothing.setImageURI(Uri.fromFile(imageFile));
                    } else {
                        imageClothing.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                } catch (Exception e) {
                    imageClothing.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                imageClothing.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        private int getCategoryIcon(String category) {
            if ("Top".equalsIgnoreCase(category)) {
                return R.drawable.ic_top;
            } else if ("Bottom".equalsIgnoreCase(category)) {
                return R.drawable.ic_bottom;
            } else if ("Shoes".equalsIgnoreCase(category)) {
                return R.drawable.ic_shoes;
            }

            return R.drawable.ic_wardrobe;
        }
    }
}
