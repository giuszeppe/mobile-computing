package com.example.expensestracker.model;
public class Expense {
    private long id;
    private String description;
    private String category;
    private String cost;
    private String date;

    public Expense(long id, String description, String category, String cost, String date) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.cost = cost;
        this.date = date;
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getCost() { return cost; }
    public String getDate() { return date; }

    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setCost(String cost) { this.cost = cost; }
    public void setDate(String date) { this.date = date; }
}
