package com.payment.personal.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity(name = "card_details")
public class CardDetails {

    @Id
    private String cardNumber;
    private int cvv;
    private String cardHolderName;
    private Date expiry;
    private String bankName;
}
