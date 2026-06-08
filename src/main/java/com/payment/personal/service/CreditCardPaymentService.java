package com.payment.personal.service;

import com.payment.personal.models.CreditCardPayments;
import com.payment.personal.models.Payments;
import com.payment.personal.repository.CreditCardPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CreditCardPaymentService {

    @Autowired
    private CreditCardPaymentRepository creditCardPaymentRepository;

    public List<CreditCardPayments> getCreditCardPayments() {
        return creditCardPaymentRepository.findAll();
    }

    public CreditCardPayments createCreditCardPayment(CreditCardPayments creditCardPayments) {
        creditCardPayments.setPaymentId(UUID.randomUUID());
        return creditCardPaymentRepository.save(creditCardPayments);
    }
}
