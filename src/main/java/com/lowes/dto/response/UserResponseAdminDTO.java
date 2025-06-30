package com.lowes.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseAdminDTO {
    UUID id;

    @NonNull
    private String name;

    @NonNull
    private String email;

    private String contact;

    private String pic;


}
