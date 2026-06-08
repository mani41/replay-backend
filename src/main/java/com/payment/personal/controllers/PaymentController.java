package com.payment.personal.controllers;

import com.payment.personal.models.CardDetails;
import com.payment.personal.models.CreditCardPayments;
import com.payment.personal.service.CreditCardPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private CreditCardPaymentService creditCardPaymentService;

    @GetMapping()
    public List<CreditCardPayments> getPayments() {

        CardDetails cardDetails = CardDetails.builder()
                .cardNumber("123456789")
                .cvv(123)
                .bankName("SBI-1")
                .expiry(null)
                .build();

        CreditCardPayments creditCardPayments = CreditCardPayments.builder()
                .paymentId(UUID.randomUUID())
                .amount(123)
                //.cardDetails(cardDetails)
                .paymentDate(null)
                .build();


        return creditCardPaymentService.getCreditCardPayments();

    }

    @PostMapping("/create-payment")
    public CreditCardPayments createCreditCardPayment(@RequestBody CreditCardPayments creditCardPayments) {
        return creditCardPaymentService.createCreditCardPayment(creditCardPayments);
    }
}
