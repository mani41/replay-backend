package com.payment.personal.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cc_payments")
public class CreditCardPayments implements Payments{

    @Id
    private UUID paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentType paymentType;
    private String cardNumber;

//    @OneToOne()
//    private CardDetails cardDetails;
}
