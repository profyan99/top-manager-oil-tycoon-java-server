package com.topmanager.oiltycoon.game.dto.response;

import com.topmanager.oiltycoon.game.model.game.scenario.Scenario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioResponseDto {
    private int id;
    private String name;
    private String description;

    public ScenarioResponseDto(Scenario scenario) {
        this(scenario.getId(), scenario.getName(), scenario.getDescription());
    }
}
