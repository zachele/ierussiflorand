package com.example.shopflowers.service.payment;

import com.example.shopflowers.model.entity.Order;

public class MockPaymentService implements PaymentService {

    @Override
    public PaymentResult processPayment(Order order) {

        if (order == null) {
            return new PaymentResult(
                    false,
                    "Ordine non valido."
            );
        }

        if (order.getTotal() <= 0) {
            return new PaymentResult(
                    false,
                    "Totale ordine non valido."
            );
        }

        if (order.getPaymentMethod() == null
                || order.getPaymentMethod().isBlank()) {

            return new PaymentResult(
                    false,
                    "Metodo di pagamento non valido."
            );
        }

        String paymentMethod = order.getPaymentMethod().trim().toUpperCase();

        boolean validPaymentMethod =
                "CARTA".equals(paymentMethod)
                        || "CONTANTI".equals(paymentMethod)
                        || "IBAN".equals(paymentMethod)
                        || "PAYPAL".equals(paymentMethod);

        if (!validPaymentMethod) {
            return new PaymentResult(
                    false,
                    "Pagamento rifiutato dal gateway esterno."
            );
        }

        return new PaymentResult(
                true,
                "Pagamento autorizzato dal gateway esterno."
        );
    }
}