package com.resresa.attributservice.web.controller;

import com.resresa.attributservice.dao.AttributeRepository;
import com.resresa.attributservice.dao.ResourceRepository;
import com.resresa.attributservice.exception.ResourceNotFoundException;
import com.resresa.attributservice.web.model.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
public class AttributeController {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @PostMapping(value = "resources/{resourceId}/attribute")
    public Optional<Attribute> createAttribute(@PathVariable(value = "resourceId") Long resourceId,
                                               @Valid @RequestBody Attribute attribute) {

        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("resource " + resourceId + " not found");
        }
        return resourceRepository.findById(resourceId).map(resource -> {
            attribute.setResource(resource);
            return attributeRepository.save(attribute);
        });
    }

    @GetMapping(value = "resources/{resourceId}/{id}")
    public Optional<Attribute> getAttributeById(@PathVariable(value = "resourceId") long resourceId,
                                                @PathVariable(value = "id") long id) {

        if (!resourceRepository.existsById(resourceId) || !attributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("resource " + resourceId + "or attribute " + id + " not found");
        }

        return attributeRepository.findByIdAndResourceId(id,resourceId);
    }

    @GetMapping(value = "resources/{resourceId}/attributes")
    public List<Attribute> getAllAttributes(@PathVariable(value = "resourceId") long resourceId){
        return attributeRepository.findAllByResourceId(resourceId);
    }

    @DeleteMapping(value = "resources/{resourceId}/{id}")
    public ResponseEntity<?> deleteAttributeById(@PathVariable(value = "id") long id,
                                                 @PathVariable(value = "resourceId") long resourceId) {

        return attributeRepository.findByIdAndResourceId(id, resourceId).map(attribute -> {
            attributeRepository.delete(attribute);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id " + id + " and resource " + resourceId));
    }

    @PutMapping(value = "resources/{resourceId}/{id}")
    public Attribute updateAttribute(@PathVariable(value = "id") long id,
                                     @PathVariable(value = "resourceId") long resourceId,
                                     @Valid @RequestBody Attribute attributeRequest) {

        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("resource " + resourceId + " not found");
        }

        return attributeRepository.findById(id).map(attribute -> {
            attribute.setName(attributeRequest.getName());
            attribute.setType(attributeRequest.getType());
            attribute.setValue(attributeRequest.getValue());
           return attributeRepository.save(attribute);
        }).orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id " + id));
    }
}
