package com.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeForm {
    private String cardName;
    private String cardNumber;
    private BigDecimal paymentAmount;
    private String currency;
    private ReservationDto reservationDto;
}
