package com.resresa.attributservice.dao;

import com.resresa.attributservice.web.model.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Organisation save(Organisation organisation);
    Organisation findById(long id);
}
