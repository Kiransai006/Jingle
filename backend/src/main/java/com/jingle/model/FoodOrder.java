package com.jingle.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class FoodOrder {

    public enum Status { PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false, length = 500)
    private String address;

    private double itemsTotal;
    private double deliveryFee;
    private double totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PLACED;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    protected FoodOrder() {
    }

    public FoodOrder(String customerName, String phone, String address) {
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    public void setTotals(double itemsTotal, double deliveryFee) {
        this.itemsTotal = itemsTotal;
        this.deliveryFee = deliveryFee;
        this.totalAmount = itemsTotal + deliveryFee;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public double getItemsTotal() { return itemsTotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }
}
