package com.lowes.dto.response.auth;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    @NonNull
    private String message;

    private String email;

    private String role;

    private String accessToken;

    private String url;
}