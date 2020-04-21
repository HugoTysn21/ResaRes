package com.resresa.authentication.controller;

import com.resresa.authentication.model.User;
import com.resresa.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @GetMapping(value = "/users")
    public Iterable<User> getUsers()
    {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @GetMapping(value = "/{organizationId}/users")
    public List<User> createUserInOrganization(@PathVariable(value = "organizationId") Integer organizationId)
    {
        return userRepository.findByOrganizationId(organizationId);
    }
}
