package com.example.wardrobeplanner.models;

public class ClothingItem {
    private int id;
    private String name;
    private String category;
    private String imageUri; // Το path της φωτογραφίας στο κινητό
    private String season;
    private String color;
    private String description;
    private int userId;

    // Constructor: Χρησιμοποιείται όταν τραβάμε δεδομένα από τη βάση
    public ClothingItem(int id, String name, String category, String imageUri) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
    }

    // Full constructor with all fields
    public ClothingItem(int id, String name, String category, String imageUri,
                        String season, String color, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
        this.season = season;
        this.color = color;
        this.description = description;
    }

    // Full constructor with userId
    public ClothingItem(int id, String name, String category, String imageUri,
                        String season, String color, String description, int userId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
        this.season = season;
        this.color = color;
        this.description = description;
        this.userId = userId;
    }

    // Getters: Απαραίτητα για να παίρνει ο Adapter τα δεδομένα και να τα δείχνει στην οθόνη
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImageUri() { return imageUri; }
    public String getSeason() { return season; }
    public String getColor() { return color; }
    public String getDescription() { return description; }
    public int getUserId() { return userId; }

    // Setters (Προαιρετικά, αν θες να αλλάζεις τα στοιχεία μετά τη δημιουργία)
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setSeason(String season) { this.season = season; }
    public void setColor(String color) { this.color = color; }
    public void setDescription(String description) { this.description = description; }
    public void setUserId(int userId) { this.userId = userId; }
}
