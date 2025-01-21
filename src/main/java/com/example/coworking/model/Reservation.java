package com.example.coworking.model;

public class Reservation {

    private int id;
    private int workspaceId;
    private String type;
    private String customerName;
    private String date;
    private String startTime;
    private String endTime;
    private double totalPrice;

    public Reservation(int id, int workspaceId, String type, String customerName, String date, String startTime, String endTime, double totalPrice) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.type = type;
        this.customerName = customerName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Reservation: " +
                "id: " + id +
                ", workspaceId: " + workspaceId +
                ", workspace type: " + type +
                ", customerName: " + customerName +
                ", date: " + date +
                ", startTime: " + startTime +
                ", endTime: " + endTime +
                ", Total Price: EUR " + totalPrice;
    }
}
