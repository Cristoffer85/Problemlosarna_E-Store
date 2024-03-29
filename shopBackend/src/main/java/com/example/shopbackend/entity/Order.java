package com.example.shopbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderQty> orderQty = new ArrayList<>();

    @Column(name = "is_basket", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private boolean activeBasket;

    /**
     * This constructor is for creating a mock database
     * @param user the user that the active basket or order belongs to
     * @param activeBasket true is a basket, false is an order
     */

    public Order(User user, boolean activeBasket) {
        this.user = user;
        this.activeBasket = activeBasket;
    }


    @Override
    public String toString() {
        return "order{" +
                "id=" + id +
                ", user=" + user +
                '}';
    }
}
