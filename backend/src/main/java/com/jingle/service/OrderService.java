package com.jingle.service;

import com.jingle.dto.OrderRequest;
import com.jingle.exception.NotFoundException;
import com.jingle.model.FoodOrder;
import com.jingle.model.MenuItem;
import com.jingle.model.OrderItem;
import com.jingle.repository.MenuItemRepository;
import com.jingle.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    public static final double DELIVERY_FEE = 30.0;

    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final Counter ordersPlaced;
    private final DistributionSummary orderValue;

    public OrderService(MenuItemRepository menuItemRepository,
                        OrderRepository orderRepository,
                        MeterRegistry meterRegistry) {
        this.menuItemRepository = menuItemRepository;
        this.orderRepository = orderRepository;
        // Business metrics exposed at /actuator/prometheus
        this.ordersPlaced = Counter.builder("jingle_orders_placed")
                .description("Number of orders placed")
                .register(meterRegistry);
        this.orderValue = DistributionSummary.builder("jingle_order_value")
                .description("Order value in INR")
                .baseUnit("inr")
                .register(meterRegistry);
    }

    @Transactional
    public FoodOrder placeOrder(OrderRequest request) {
        FoodOrder order = new FoodOrder(request.customerName(), request.phone(), request.address());
        double itemsTotal = 0;

        for (OrderRequest.Line line : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(line.menuItemId())
                    .orElseThrow(() -> new NotFoundException("Menu item not found: " + line.menuItemId()));
            OrderItem item = new OrderItem(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), line.quantity());
            order.addItem(item);
            itemsTotal += item.getLineTotal();
        }

        order.setTotals(itemsTotal, DELIVERY_FEE);
        FoodOrder saved = orderRepository.save(order);

        ordersPlaced.increment();
        orderValue.record(saved.getTotalAmount());
        return saved;
    }

    @Transactional(readOnly = true)
    public FoodOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FoodOrder> listOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
}
