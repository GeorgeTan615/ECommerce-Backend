package com.george.reservationservice.model;

import com.george.reservationservice.dto.CartDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="Reservation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    private String id;
    private String userId;
    private LocalDateTime expirationDateTime;

    @OneToOne
    private Cart cart;

}
