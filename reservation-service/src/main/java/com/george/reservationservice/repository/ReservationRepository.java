package com.george.reservationservice.repository;

import com.george.reservationservice.model.Inventory;
import com.george.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findAllByExpirationDateTimeBefore(LocalDateTime localDateTime);

    // Delete all at once, JPA will do batch delete which is faster than delete one by one
    void deleteAll(List<Reservation> reservations);

}
