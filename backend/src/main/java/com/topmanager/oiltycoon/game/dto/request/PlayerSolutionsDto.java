package com.topmanager.oiltycoon.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PlayerSolutionsDto {
    private int price;
    private int production;
    private int marketing;
    private int investments;
    private int nir;
}
