package com.example.coworking.model;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspaceId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private Time startTime;

    @Column(nullable = false)
    private Time endTime;

    @Column(nullable = false)
    private double totalPrice;

    public Reservation() {}

    public Reservation(Workspace workspaceId, String type, String customerName, String date, Time startTime, Time endTime, double totalPrice) {
        this.workspaceId = workspaceId;
        this.type = type;
        this.customerName = customerName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
    }

    //<editor-fold desc="Getters/Setters">
    public int getId() {
        return id;
    }

    public Workspace getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Workspace workspace) {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Reservation: " +
                "id: " + id +
                ", workspace: " + workspaceId +
                ", workspace type: " + type +
                ", customerName: " + customerName +
                ", date: " + date +
                ", startTime: " + startTime +
                ", endTime: " + endTime +
                ", Total Price: EUR " + totalPrice;
    }
}
