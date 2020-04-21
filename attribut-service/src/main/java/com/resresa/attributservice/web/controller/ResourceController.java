package com.resresa.attributservice.web.controller;

import com.resresa.attributservice.dao.AttributeRepository;
import com.resresa.attributservice.dao.OrganisationRepository;
import com.resresa.attributservice.dao.ResourceRepository;
import com.resresa.attributservice.exception.ResourceNotFoundException;
import com.resresa.attributservice.web.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Transactional
@RestController
public class ResourceController {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    AttributeRepository attributeRepository;

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @PostMapping(value = "organisation/{organisationId}/resource")
    public Optional<ResponseEntity<Object>> createResource(@PathVariable(value = "organisationId") Long organisationId,
                                                           @Valid @RequestBody Resource resource) {

        if (!organisationRepository.existsById(organisationId)) {
            throw new ResourceNotFoundException("organisation " + organisationId + " not found");
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resource.getId())
                .toUri();


        return organisationRepository.findById(organisationId).map(organisation -> {
            resource.setOrganisation(organisation);
            System.out.println(resource);
            resourceRepository.save(resource);
            return ResponseEntity.created(location).build();
        });
    }

    @GetMapping(value = "organisation/{organisationId}/{resourceId}")
    public Optional<Resource> getResourceById(@PathVariable(value = "organisationId") long organisationId,
                                              @PathVariable(value = "resourceId") long resourceId) {

        if (!organisationRepository.existsById(organisationId) || !resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("organisation " + organisationId + "or resource " + resourceId + " not found");
        }

        return resourceRepository.findByIdAndOrganisationId(resourceId, organisationId);
    }

    @GetMapping(value = "organisation/{organisationId}/resources")
    public List<Resource> getResourcesByOrganisation(@PathVariable(value = "organisationId") int organisationId) {

        if (!organisationRepository.existsById((long) organisationId)) {
            throw new ResourceNotFoundException("organisation " + organisationId + " not found");
        }
        return resourceRepository.findAllByOrganisationId((long) organisationId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @DeleteMapping(value = "organisation/{organisationId}/{resourceId}")
    public ResponseEntity<?> deleteResource(@PathVariable(value = "organisationId") Long organisationId,
                                            @PathVariable(value = "resourceId") Long resourceId) {

        return resourceRepository.findByIdAndOrganisationId(resourceId, organisationId).map(resource -> {
            attributeRepository.deleteAllByResourceId(resourceId);
            resourceRepository.delete(resource);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Resource not found with id " + resourceId + " and organisation " + organisationId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @PutMapping(value = "organisation/{organisationId}/{resourceId}")
    public Resource updateResource(@PathVariable(value = "organisationId") long organisationId,
                                   @PathVariable(value = "resourceId") long resourceId,
                                   @Valid @RequestBody Resource resourceRequest) {

        if (!organisationRepository.existsById(organisationId)) {
            throw new ResourceNotFoundException("organisation " + organisationId + " not found");
        }

        return resourceRepository.findById(resourceId).map(resource -> {
            resource.setName(resourceRequest.getName());
            return resourceRepository.save(resource);
        }).orElseThrow(() -> new ResourceNotFoundException("Resource " + resourceId + "not found"));
    }
}

