package com.topmanager.oiltycoon.social.dto.request;

import com.topmanager.oiltycoon.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    @Length(min = 6, max = 25, message = Utils.NAME_NOT_VALID_MESSAGE)
    private String userName;

    @NotNull(message = Utils.NAME_NOT_VALID_MESSAGE)
    @NotBlank(message = Utils.NAME_NOT_VALID_MESSAGE)
    @Length(min = 6, max = 25, message = Utils.NAME_NOT_VALID_MESSAGE)
    private String password;

    private String firstName;
    private String lastName;

    @Email(regexp = Utils.EMAIL_REGEX, message = Utils.EMAIL_NOT_VALID)
    private String email;

    private String description;

    private String avatar;

    private String country;

}
