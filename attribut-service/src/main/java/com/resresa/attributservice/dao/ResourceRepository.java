package com.resresa.attributservice.dao;

import com.resresa.attributservice.web.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByIdAndOrganisationId(Long id, Long organisation_Id);
    List<Resource> findAllByOrganisationId(Long organisation_Id);
    Optional<Resource> deleteAllByOrganisationId(Long organisation_Id);
}
