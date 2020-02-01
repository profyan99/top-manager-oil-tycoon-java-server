package com.topmanager.oiltycoon.game.dto;

import com.topmanager.oiltycoon.game.model.game.company.Company;
import com.topmanager.oiltycoon.game.model.game.company.CompanyStatistics;
import com.topmanager.oiltycoon.game.model.game.company.CompanyStore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyDto {
    private String name;
    private CompanyStatistics statistics;
    private CompanyStore companyStore;
    private int bank;

    public CompanyDto(Company company) {
        this(company.getName(), company.getStatistics(), company.getStore(), company.getBank());
    }
}
