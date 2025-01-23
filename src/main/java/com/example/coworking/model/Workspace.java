package com.example.coworking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String status;

    public Workspace() {}

    public Workspace(String type, double price, String status) {
        this.type = type;
        this.price = price;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Workspace: " +
                "id: " + id +
                ", type: " + type +
                ", price for 1h: EUR " + price;
    }
}
