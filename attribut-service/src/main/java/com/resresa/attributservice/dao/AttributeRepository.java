package com.resresa.attributservice.dao;

import com.resresa.attributservice.web.model.Attribute;
import com.resresa.attributservice.web.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

//    Attribute  save(Attribute attribute);
//    Attribute findById(long id);
//    void deleteById(long id);
    Optional<Attribute> findByIdAndResourceId(Long id, Long resource_id);
    List<Attribute> findAllByResourceId(Long resource_id);
    Optional<Attribute> deleteAllByResourceId(Long resource_id);
}
