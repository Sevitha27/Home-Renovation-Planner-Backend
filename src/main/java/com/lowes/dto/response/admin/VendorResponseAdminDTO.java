package com.lowes.dto.response.admin;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponseAdminDTO {
    @NonNull
    private UUID exposedId;

    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String contact;

    private String pic;

    @NonNull
    private String companyName;

    @NonNull
    private Boolean available;

    private Boolean approved;

    @NonNull
    private String experience;

    private List<String> skills;
}
