package com.topmanager.oiltycoon.social.dto.request;

import com.topmanager.oiltycoon.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEditRequestDto {
    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    @Length(min = 6, max = 25, message = Utils.NAME_NOT_VALID_MESSAGE)
    private String userName;

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    private String firstName;

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    private String lastName;

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    private String oldPassword;

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    private String newPassword;
}
