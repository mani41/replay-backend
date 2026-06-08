package com.payment.personal.repository;

import com.payment.personal.models.CreditCardPayments;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardPaymentRepository extends JpaRepository<CreditCardPayments, Id> {
}
