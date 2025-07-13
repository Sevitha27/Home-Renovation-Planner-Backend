package com.lowes.dto.response.admin;

import lombok.*;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseAdminDTO {

    @NonNull
    private UUID exposedId;

    @NonNull
    private String name;

    @NonNull
    private String email;

    private String contact;

    private String pic;


}
