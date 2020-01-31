package com.topmanager.oiltycoon.game.dto;

import com.topmanager.oiltycoon.game.model.game.Company;
import com.topmanager.oiltycoon.game.model.game.CompanyStatistics;
import com.topmanager.oiltycoon.game.model.game.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompanyDto {
    private String name;
    private CompanyStatistics statistics;
    private Store store;
    private int bank;

    public CompanyDto(Company company) {
        this(company.getName(), company.getStatistics(), company.getStore(), company.getBank());
    }
}
