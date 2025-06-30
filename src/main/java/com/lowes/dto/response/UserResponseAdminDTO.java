package com.lowes.dto.response;

import jakarta.persistence.Column;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseAdminDTO {
    @NonNull
    private String name;

    @NonNull
    private String email;

    private String contact;

    private String pic;


}
