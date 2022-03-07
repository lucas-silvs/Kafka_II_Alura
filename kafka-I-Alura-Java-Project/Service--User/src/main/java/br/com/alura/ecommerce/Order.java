package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

    private final  String userId;
    private final  String orderId;
    private final BigDecimal amount;

    public Order(String userId, String orderId, BigDecimal value) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = value;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "br.com.alura.ecommerce.Order{" +
                "userId='" + userId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getEmail() {
        return "Suposto email";
    }
}
