package com.samee.server.service;



import com.samee.server.dto.CompanyDto;

import java.util.List;

public interface CompanyService {
    void registerCompany(CompanyDto companyDto);
    String login(CompanyDto companyDto);
    List<CompanyDto> getAllCompanies();
    CompanyDto deleteCompany(String name);
    CompanyDto getCompanyByName(String username);
}
