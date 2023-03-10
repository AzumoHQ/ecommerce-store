package com.azumo.MyStore.sales.order.jdbc;

import com.azumo.MyStore.common.events.EventPublisher;
import com.azumo.MyStore.common.primitives.Money;
import com.azumo.MyStore.sales.order.OrderId;
import com.azumo.MyStore.sales.order.PlaceOrder;
import com.azumo.MyStore.sales.order.item.OrderItem;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * JDBC implementation for Place Order use-cases.
 */
@RequiredArgsConstructor
@Slf4j
class PlaceOrderJdbc implements PlaceOrder {

    private final @NonNull JdbcTemplate jdbcTemplate;
    private final @NonNull EventPublisher eventPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void place(@NonNull OrderId orderId, @NonNull Collection<OrderItem> items, @NonNull Money total) {
        new OrderJdbc(orderId, total, items, jdbcTemplate, eventPublisher)
                .place();
    }
}
