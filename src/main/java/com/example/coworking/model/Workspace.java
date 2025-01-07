package com.example.coworking.model;

public class Workspace {
    private int id;
    private String type;
    private double price;

    public Workspace(int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Workspace: " +
                "id: " + id +
                ", type: " + type +
                ", price for 1h: EUR " + price;
    }
}
