package com.topmanager.oiltycoon.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ScenarioAddRequestDto {
    @NotNull
    @NotBlank
    @Length(min = 6)
    private String name;

    @NotNull
    @NotBlank
    @Length(min = 24)
    private String description;

    private int loanLimit;
    private int extraLoanLimit;
    private double bankRate;
    private double extraBankRate;
}
