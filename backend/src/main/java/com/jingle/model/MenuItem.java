package com.jingle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    private double price;
    private boolean veg;
    private boolean bestseller;

    @Column(length = 500)
    private String imageUrl;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    protected MenuItem() {
    }

    public MenuItem(String name, String description, double price, boolean veg,
                    boolean bestseller, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.veg = veg;
        this.bestseller = bestseller;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public boolean isVeg() { return veg; }
    public boolean isBestseller() { return bestseller; }
    public String getImageUrl() { return imageUrl; }
    public Restaurant getRestaurant() { return restaurant; }

    void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    // used by unit tests
    public void setId(Long id) { this.id = id; }
}
