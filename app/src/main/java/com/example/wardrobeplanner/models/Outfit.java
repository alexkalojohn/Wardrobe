package com.example.wardrobeplanner.models;

import java.util.List;

public class Outfit {
    private int id;
    private String outfitName;
    private List<ClothingItem> items; // Λίστα με τα ρούχα που αποτελούν το σύνολο

    public Outfit(int id, String outfitName, List<ClothingItem> items) {
        this.id = id;
        this.outfitName = outfitName;
        this.items = items;
    }

    public int getId() { return id; }
    public String getOutfitName() { return outfitName; }
    public List<ClothingItem> getItems() { return items; }
}