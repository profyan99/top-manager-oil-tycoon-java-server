package com.topmanager.oiltycoon.dto.response;

import com.topmanager.oiltycoon.dto.ErrorDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    List<ErrorDto> errors;
}
