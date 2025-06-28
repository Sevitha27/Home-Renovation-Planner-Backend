package com.lowes.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {

    @NonNull
    private String message;

    @NonNull
    private String email;


    private String accessToken;

}