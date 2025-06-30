package com.lowes.dto.response;

import com.lowes.entity.Skill;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponseAdminDTO {
    UUID id;

    private String email;
    private String contact;
    private String pic;

    private String companyName;
    private Boolean available;
    private Boolean approved;
    private String experience;

    private List<String> skills;
}
