package com.samee.server.controller;


import com.samee.server.dto.CompanyDto;
import com.samee.server.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173" ,allowCredentials = "true")
@RestController
@RequestMapping("api/v1/company")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CompanyDto companyDto) {
        try {
            companyService.registerCompany(companyDto);
            return new ResponseEntity<>(companyDto.getName() + " company registered successfully!", HttpStatus.CREATED);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginCompany(@RequestBody CompanyDto companyDto) {
        Map<String, String> tokenResponse = companyService.login(companyDto);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        try {
            List<CompanyDto> companies = companyService.getAllCompanies();

            if (companies.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(companies);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/{name}")
    public ResponseEntity<CompanyDto> getCompanyByName(@PathVariable String name) {
        try {
            CompanyDto company = companyService.getCompanyByName(name);
            return ResponseEntity.ok(company);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCompany(@PathVariable String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated User Role: " + authentication.getAuthorities());

        try {
            CompanyDto deletedCompany = companyService.deleteCompany(name);
            return ResponseEntity.ok(deletedCompany.getName() + " Company successfully deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found: " + e.getMessage());
        }
    }
}