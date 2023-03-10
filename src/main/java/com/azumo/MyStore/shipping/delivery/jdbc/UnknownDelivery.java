package com.azumo.MyStore.shipping.delivery.jdbc;

import com.azumo.MyStore.shipping.delivery.*;
import lombok.ToString;

/**
 * Null object implementation for Delivery entity.
 */
@ToString
final class UnknownDelivery implements Delivery {

    @Override
    public DeliveryId id() {
        return new DeliveryId(0);
    }

    @Override
    public OrderId orderId() {
        return new OrderId(0);
    }

    @Override
    public Address address() {
        return new Address(
                new Person("Unknown Person"),
                new Place("Unknown"));
    }

    @Override
    public void prepare() {
        // do nothing
    }

    @Override
    public void dispatch() {
        // do nothing
    }

    @Override
    public boolean isDispatched() {
        return false;
    }
}
