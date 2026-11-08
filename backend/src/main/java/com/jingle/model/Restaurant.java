package com.jingle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String cuisine;
    private String area;
    private double rating;
    private int deliveryMinutes;
    private int costForTwo;

    @Column(length = 500)
    private String imageUrl;

    private String offer;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menu = new ArrayList<>();

    protected Restaurant() {
    }

    public Restaurant(String name, String cuisine, String area, double rating,
                      int deliveryMinutes, int costForTwo, String imageUrl, String offer) {
        this.name = name;
        this.cuisine = cuisine;
        this.area = area;
        this.rating = rating;
        this.deliveryMinutes = deliveryMinutes;
        this.costForTwo = costForTwo;
        this.imageUrl = imageUrl;
        this.offer = offer;
    }

    public Restaurant addMenuItem(MenuItem item) {
        item.setRestaurant(this);
        menu.add(item);
        return this;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCuisine() { return cuisine; }
    public String getArea() { return area; }
    public double getRating() { return rating; }
    public int getDeliveryMinutes() { return deliveryMinutes; }
    public int getCostForTwo() { return costForTwo; }
    public String getImageUrl() { return imageUrl; }
    public String getOffer() { return offer; }
    public List<MenuItem> getMenu() { return menu; }
}
