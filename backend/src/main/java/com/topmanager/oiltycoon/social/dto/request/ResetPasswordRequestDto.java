package com.topmanager.oiltycoon.social.dto.request;

import com.topmanager.oiltycoon.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    private String token;


    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    @Length(min = 6, max = 25, message = Utils.NAME_NOT_VALID_MESSAGE)
    private String newPassword;
}
