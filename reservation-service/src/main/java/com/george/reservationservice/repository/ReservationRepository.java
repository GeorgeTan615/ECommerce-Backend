package com.george.reservationservice.repository;

import com.george.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    void deleteByExpirationDateTimeBefore(LocalDateTime localDateTime);
}
