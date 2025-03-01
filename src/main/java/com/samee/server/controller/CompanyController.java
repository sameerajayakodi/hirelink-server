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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/company")
// Removed the @CrossOrigin annotation since CORS is now handled globally
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
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", companyDto.getName() + " company registered successfully!"));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", exception.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginCompany(@RequestBody CompanyDto companyDto) {
        Map<String, String> tokenResponse = companyService.login(companyDto);
        return ResponseEntity.ok(tokenResponse);
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> getAllCompanies() {
        List<CompanyDto> companies = companyService.getAllCompanies();
        return companies.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(companies);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getCompanyByName(@PathVariable String name) {
        Optional<CompanyDto> company = Optional.ofNullable(companyService.getCompanyByName(name));
        return company.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((CompanyDto) Map.of("error", "Company not found")));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCompanyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyName = authentication.getName();

        Optional<CompanyDto> company = Optional.ofNullable(companyService.getCompanyByName(companyName));
        return company.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body((CompanyDto) Map.of("error", "Company profile not found")));
    }

    @DeleteMapping("/delete/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCompany(@PathVariable String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authenticated User Role: " + authentication.getAuthorities());

        try {
            CompanyDto deletedCompany = companyService.deleteCompany(name);
            return ResponseEntity.ok(Map.of("message", deletedCompany.getName() + " Company successfully deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Company not found: " + e.getMessage()));
        }
    }
}