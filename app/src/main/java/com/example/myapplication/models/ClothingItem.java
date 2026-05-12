package com.example.myapplication.models;

public class ClothingItem {
    private int id;
    private String name;
    private String category;
    private String imageUri; // Το path της φωτογραφίας στο κινητό

    // Constructor: Χρησιμοποιείται όταν τραβάμε δεδομένα από τη βάση
    public ClothingItem(int id, String name, String category, String imageUri) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUri = imageUri;
    }

    // Getters: Απαραίτητα για να παίρνει ο Adapter τα δεδομένα και να τα δείχνει στην οθόνη
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImageUri() { return imageUri; }

    // Setters (Προαιρετικά, αν θες να αλλάζεις τα στοιχεία μετά τη δημιουργία)
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}