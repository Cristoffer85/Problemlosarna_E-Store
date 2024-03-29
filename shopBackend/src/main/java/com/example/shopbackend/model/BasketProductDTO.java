package com.example.shopbackend.model;

import com.example.shopbackend.entity.OrderQty;
import lombok.Getter;

@Getter
public class BasketProductDTO {

    private final Long id;
    private final int quantity;
    private final String name;
    private final String description;
    private final int price;

    public BasketProductDTO(OrderQty item) {
        this.id = item.getProduct().getId();
        this.quantity = item.getQuantity();
        this.name = item.getProduct().getName();
        this.description = item.getProduct().getDescription();
        this.price = item.getProduct().getPrice();
    }

    @Override
    public String toString() {
        return "BasketProductDTO{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
