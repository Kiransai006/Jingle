package com.jingle.service;

import com.jingle.dto.OrderRequest;
import com.jingle.exception.NotFoundException;
import com.jingle.model.FoodOrder;
import com.jingle.model.MenuItem;
import com.jingle.repository.MenuItemRepository;
import com.jingle.repository.OrderRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private MenuItemRepository menuItemRepository;
    private OrderRepository orderRepository;
    private SimpleMeterRegistry registry;
    private OrderService service;

    @BeforeEach
    void setUp() {
        menuItemRepository = mock(MenuItemRepository.class);
        orderRepository = mock(OrderRepository.class);
        registry = new SimpleMeterRegistry();
        service = new OrderService(menuItemRepository, orderRepository, registry);
        when(orderRepository.save(any(FoodOrder.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private MenuItem item(long id, String name, double price) {
        MenuItem m = new MenuItem(name, "desc", price, true, false, "img");
        m.setId(id);
        return m;
    }

    @Test
    void placeOrderCalculatesTotalsAndRecordsMetrics() {
        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(item(1L, "Pesarattu", 89)));
        when(menuItemRepository.findById(2L)).thenReturn(Optional.of(item(2L, "Pulihora", 129)));

        OrderRequest request = new OrderRequest("Kiran", "0870000000", "Dublin",
                List.of(new OrderRequest.Line(1L, 2), new OrderRequest.Line(2L, 1)));

        FoodOrder order = service.placeOrder(request);

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItemsTotal()).isEqualTo(307.0);
        assertThat(order.getDeliveryFee()).isEqualTo(OrderService.DELIVERY_FEE);
        assertThat(order.getTotalAmount()).isEqualTo(337.0);
        assertThat(order.getStatus()).isEqualTo(FoodOrder.Status.PLACED);
        assertThat(registry.get("jingle_orders_placed").counter().count()).isEqualTo(1.0);
    }

    @Test
    void placeOrderFailsForUnknownMenuItem() {
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        OrderRequest request = new OrderRequest("Kiran", "0870000000", "Dublin",
                List.of(new OrderRequest.Line(99L, 1)));

        assertThatThrownBy(() -> service.placeOrder(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderFailsWhenMissing() {
        when(orderRepository.findById(5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getOrder(5L)).isInstanceOf(NotFoundException.class);
    }
}
