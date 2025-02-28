package com.samee.server.service.impl;


import com.samee.server.entity.Company;
import com.samee.server.repo.CompanyRepo;
import com.samee.server.utils.UserRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CompanyDetailsServiceImpl implements UserDetailsService {

    private final CompanyRepo companyRepository;

    @Autowired
    public CompanyDetailsServiceImpl(CompanyRepo companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Company company = companyRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Company not found with name: " + username));

        return new User(
                company.getName(),
                company.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(UserRoles.COMPANY.name()))
        );
    }
}
