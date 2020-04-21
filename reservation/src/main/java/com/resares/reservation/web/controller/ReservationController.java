package com.resares.reservation.web.controller;

import com.resares.reservation.dao.ReservationRepository;
import com.resares.reservation.exception.ResourceNotFoundException;
import com.resares.reservation.security.SecurityTokenConfig;
import com.resares.reservation.web.model.JwtConfig;
import com.resares.reservation.web.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SecurityTokenConfig jwtTokenProvider;

    @Autowired
    JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private String token = null;

    @PostMapping(value = "{organisationId}/{resourceId}/reservation")
    public ResponseEntity<?> reservation(@PathVariable(value = "resourceId") long resourceId,
                                         @PathVariable(value = "organisationId") long organisationId,
                                         @RequestHeader(value = "Authorization") String authorizationHeader,
                                         @Valid @RequestBody Reservation reservationRequest) {

        if (StringUtils.hasText(jwtConfig.getPrefix()) && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            token =  authorizationHeader.substring(7);
        }
        Long userIdToken = jwtTokenProvider.getUserIdFromJWT(token);

        reservationRequest.setUserId(userIdToken);
        reservationRequest.setResourceId(resourceId);
        reservationRequest.setOrganisationId(organisationId);
        reservationRepository.save(reservationRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(reservationRequest.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "organisation/{organisationId}/reservations")
    public List<Reservation> getAllReservation(@PathVariable(value = "organisationId") long organisationId) {

        return reservationRepository.findAllByOrganisationId(organisationId);
    }

    @GetMapping(value = "{userId}/reservations")
    public List<Reservation> getUserReservation(@PathVariable(value = "userId") long userId,
                                                    @RequestHeader(value = "Authorization") String authorizationHeader) {

        if (StringUtils.hasText(jwtConfig.getPrefix()) && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            token =  authorizationHeader.substring(7);
        }

        Long userIdToken = jwtTokenProvider.getUserIdFromJWT(token);
        if (!userIdToken.equals(userId) ){
            return Collections.emptyList();
        }

        return reservationRepository.findAllByUserId(userId);
    }

    @GetMapping(value = "resource/{resourceId}/reservations")
    public Optional<Reservation> getResourceReservation(@PathVariable(value = "resourceId") long resourceId) {

        return reservationRepository.findByResourceId(resourceId);
    }

    @PutMapping(value = "update/{reservationId}")
    public Reservation updateReservation(@PathVariable(value = "reservationId") long reservationId,
                                         @Valid @RequestBody Reservation reservationRequest,
                                         @RequestHeader(value = "Authorization") String authorizationHeader) {

        if (StringUtils.hasText(jwtConfig.getPrefix()) && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            token =  authorizationHeader.substring(7);
        }

        Long userIdToken = jwtTokenProvider.getUserIdFromJWT(token);

        return reservationRepository.findById(reservationId).map(reservation -> {

            if (reservation.getUserId() == userIdToken) {
                reservation.setName(reservationRequest.getName());
                reservation.setStart_date(reservationRequest.getStart_date());
                reservation.setEnd_date(reservationRequest.getEnd_date());
            } else {
                return null;

            }
            return reservationRepository.save(reservation);
        }).orElseThrow(() -> new ResourceNotFoundException("Resource " + reservationId + "not found"));
    }

    @DeleteMapping(value = "delete/{reservationId}")
    public ResponseEntity<?> deleteReservation(@PathVariable(value = "reservationId") long reservationId,
                                               @RequestHeader(value = "Authorization") String authorizationHeader) {

        if (StringUtils.hasText(jwtConfig.getPrefix()) && authorizationHeader.startsWith(jwtConfig.getPrefix())) {
            token =  authorizationHeader.substring(7);
        }

        Long userIdToken = jwtTokenProvider.getUserIdFromJWT(token);
        String roleUser = jwtTokenProvider.getRoleFromJWT(token);

        return reservationRepository.findById(reservationId).map(reservation -> {

            if (reservation.getUserId() == userIdToken) {
                reservationRepository.deleteById(reservationId);
                return ResponseEntity.ok().build();
            } else {
                return null;
            }
        }).orElseThrow(() -> new ResourceNotFoundException("Reservation " + reservationId + "not found"));
    }
}
