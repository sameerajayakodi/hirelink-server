package com.samee.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/health")
public class ServiceStatus {
    @GetMapping
    public ResponseEntity<String> serverStatus(HttpServletRequest request){
        CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");

        return new ResponseEntity<>("server up and running....!!! id is: "
                +request.getSession().getId()+" CSRF token : "+ csrf, HttpStatus.OK);
    }
}
