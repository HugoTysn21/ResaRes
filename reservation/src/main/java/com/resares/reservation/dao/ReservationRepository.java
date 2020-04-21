package com.resares.reservation.dao;

import com.resares.reservation.web.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByUserId(long userId);
    List<Reservation> findAllByOrganisationId(long organisationId);
    Optional<Reservation> findByResourceId(long id);
}
