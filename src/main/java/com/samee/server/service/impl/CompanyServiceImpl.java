package com.samee.server.service.impl;

import com.samee.server.dto.CompanyDto;
import com.samee.server.entity.Company;
import com.samee.server.repo.CompanyRepo;
import com.samee.server.service.CompanyService;
import com.samee.server.service.auth.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Autowired
    public CompanyServiceImpl(CompanyRepo companyRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void registerCompany(CompanyDto companyDto) {
        // Check if company already exists
        if (companyRepository.findByName(companyDto.getName()).isPresent()) {
            throw new RuntimeException("Company with name " + companyDto.getName() + " already exists");
        }

        // Create new company entity
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setEmail(companyDto.getEmail());
        company.setPassword(passwordEncoder.encode(companyDto.getPassword()));
        company.setDescription(companyDto.getDescription());
        company.setIndustry(companyDto.getIndustry());
        company.setLocation(companyDto.getLocation());
        company.setWebsite(companyDto.getWebsite());

        // Save the company
        companyRepository.save(company);
    }

    @Override
    public Map<String, String> login(CompanyDto companyDto) {
        // Find company by name
        Optional<Company> optionalCompany = companyRepository.findByName(companyDto.getName());

        if (optionalCompany.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        Company company = optionalCompany.get();

        // Verify password
        if (!passwordEncoder.matches(companyDto.getPassword(), company.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(company.getName(), "COMPANY");

        // Create response with token
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDto deleteCompany(String name) {
        Optional<Company> optionalCompany = companyRepository.findByName(name);

        if (optionalCompany.isEmpty()) {
            throw new RuntimeException("Company not found with name: " + name);
        }

        Company company = optionalCompany.get();
        CompanyDto companyDto = convertToDto(company);

        companyRepository.delete(company);

        return companyDto;
    }

    @Override
    public CompanyDto getCompanyByName(String name) {
        return companyRepository.findByName(name)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Company not found with name: " + name));
    }

    @Override
    public CompanyDto updateCompany(String name, CompanyDto companyDto) {
        // Find company by name
        Company company = companyRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Company not found with name: " + name));

        // Update company fields (excluding name as it's used as an identifier)
        // Only update fields that are provided in the DTO
        if (companyDto.getEmail() != null) {
            company.setEmail(companyDto.getEmail());
        }

        if (companyDto.getDescription() != null) {
            company.setDescription(companyDto.getDescription());
        }

        if (companyDto.getIndustry() != null) {
            company.setIndustry(companyDto.getIndustry());
        }

        if (companyDto.getLocation() != null) {
            company.setLocation(companyDto.getLocation());
        }

        if (companyDto.getWebsite() != null) {
            company.setWebsite(companyDto.getWebsite());
        }

        // Update password if provided
        if (companyDto.getPassword() != null && !companyDto.getPassword().isEmpty()) {
            company.setPassword(passwordEncoder.encode(companyDto.getPassword()));
        }

        // Save updated company
        Company updatedCompany = companyRepository.save(company);

        // Return updated company as DTO
        return convertToDto(updatedCompany);
    }

    // Helper method to convert entity to DTO
    private CompanyDto convertToDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setEmail(company.getEmail());
        dto.setDescription(company.getDescription());
        dto.setIndustry(company.getIndustry());
        dto.setLocation(company.getLocation());
        dto.setWebsite(company.getWebsite());
        // Don't set password for security reasons
        return dto;
    }
}