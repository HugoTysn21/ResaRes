package com.resares.reservation.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping(value = "/information")
    public ResponseEntity<String> getInfos() {

        return ResponseEntity.accepted().body(
                "Welcome in our solution. Si vous voulez voir nos produits veuillez vous enregistré ");
    }
}
