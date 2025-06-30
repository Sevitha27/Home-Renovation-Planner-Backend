package com.lowes.dto.response;

import com.lowes.entity.Skill;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorResponseAdminDTO {
    private String email;
    private String contact;
    private String pic;

    private String companyName;
    private Boolean available;
    private Boolean approved;
    private String experience;

    private List<String> skills;
}
