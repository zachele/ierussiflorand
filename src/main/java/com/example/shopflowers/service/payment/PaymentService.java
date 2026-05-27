package com.example.shopflowers.service.payment;

import com.example.shopflowers.model.entity.Order;

public interface PaymentService {

    PaymentResult processPayment(Order order);
}