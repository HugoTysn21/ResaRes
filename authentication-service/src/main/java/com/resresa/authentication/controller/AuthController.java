package com.resresa.authentication.controller;

import com.resresa.authentication.exception.AppException;
import com.resresa.authentication.model.Role;
import com.resresa.authentication.model.RoleName;
import com.resresa.authentication.model.User;
import com.resresa.authentication.payload.ApiResponse;
import com.resresa.authentication.payload.JwtAuthenticationResponse;
import com.resresa.authentication.payload.LoginRequest;
import com.resresa.authentication.payload.SignUpRequest;
import com.resresa.authentication.repository.RoleRepository;
import com.resresa.authentication.repository.UserRepository;
import com.resresa.authentication.security.JwtTokenProvider;
import com.resresa.authentication.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        String role = authentication.getAuthorities().iterator().next().toString();

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Optional<User> userLogged = userRepository.findByUsername(userPrincipal.getUsername());

        if(userLogged.isPresent()){
            User user = userLogged.get();
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, role, user.getOrganizationId(), user.getId()));
        }

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, role, null, null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user")
    public ResponseEntity<?> registerEditorUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Nom de compte déjà utilisé !"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Adresse email déjà utlisée !"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getOrganizationId());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info(""+roleRepository.findByName(RoleName.ROLE_EDITOR));
        Role userRole = roleRepository.findByName(RoleName.ROLE_EDITOR)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Utlisateur créer avec succés."));
    }

    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    @PostMapping("/{organizationId}/user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, @PathVariable(value = "organizationId") Integer organizationId) {

        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Nom de compte déjà utilisé !"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Adresse email déjà utlisée !"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getOrganizationId());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        logger.info(""+roleRepository.findByName(RoleName.ROLE_USER));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException("User Role not set."));

        user.setRoles(Collections.singleton(userRole));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "Utlisateur créer avec succés."));
    }
}
