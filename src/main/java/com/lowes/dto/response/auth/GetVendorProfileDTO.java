package com.lowes.dto.response.auth;

import com.lowes.dto.request.SkillRequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetVendorProfileDTO {
    private String name;
    private String contact;
    private String url;
    private String email;

    private String companyName;
    private String experience;
    private Boolean available;
    private List<SkillRequestDTO> skills;
}

