package com.lowes.dto.request.auth;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginDTO {
    @NonNull
    private String email;

    @NonNull
    private String password;
}
