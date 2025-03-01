package com.samee.server.service;



import com.samee.server.dto.CompanyDto;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    void registerCompany(CompanyDto companyDto);
    Map<String, String> login(CompanyDto companyDto);
    List<CompanyDto> getAllCompanies();
    CompanyDto deleteCompany(String name);
    CompanyDto getCompanyByName(String username);
}
