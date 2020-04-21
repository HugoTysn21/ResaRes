package com.resresa.attributservice.web.controller;

import com.resresa.attributservice.dao.OrganisationRepository;
import com.resresa.attributservice.web.model.Organisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@RestController
public class OrganisationController {


    @Autowired
    private OrganisationRepository organisationRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganisationController.class);

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/organisation")
    public ResponseEntity<String> createOrganisation(@Valid @RequestBody Organisation organisation) {
        if (organisation == null) {
            return ResponseEntity.badRequest().body("organisation est null");
        }

        organisationRepository.save(organisation);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(organisation.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/organisations")
    public Iterable<Organisation> getOrganisation() {

        return organisationRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @GetMapping(value = "/organisation/{id}")
    public Organisation getOrganisationById(@PathVariable(value = "id") long id) {

        return organisationRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/organisation/{id}")
    public ResponseEntity<?> deleteOrganisation(@PathVariable(value = "id") long id) {

        // TODO : faire en sorte de supprimé aussi toute les ressource et attribut associé a l'organisation

        organisationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
