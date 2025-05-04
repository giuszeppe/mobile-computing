package com.example.expensestracker.model;

public class Category {
    private final long id;
    private String name;
    private String colorHex;

    public Category(long id, String name, String colorHex) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
    }
    public long getId() {return id; }
    public String getName() { return name; }
    public String getColorHex() { return colorHex; }

    public void setName(String name) { this.name = name; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}
