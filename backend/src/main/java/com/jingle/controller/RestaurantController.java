package com.jingle.controller;

import com.jingle.exception.NotFoundException;
import com.jingle.model.MenuItem;
import com.jingle.model.Restaurant;
import com.jingle.repository.MenuItemRepository;
import com.jingle.repository.RestaurantRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantController(RestaurantRepository restaurantRepository,
                                MenuItemRepository menuItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping
    public List<Restaurant> list(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return restaurantRepository.findAll();
        }
        return restaurantRepository.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCase(search, search);
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found: " + id));
    }

    @GetMapping("/{id}/menu")
    public List<MenuItem> menu(@PathVariable Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new NotFoundException("Restaurant not found: " + id);
        }
        return menuItemRepository.findByRestaurantIdOrderByBestsellerDescNameAsc(id);
    }
}
