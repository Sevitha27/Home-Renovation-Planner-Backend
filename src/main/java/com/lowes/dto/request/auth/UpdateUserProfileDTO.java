package com.lowes.dto.request.auth;

import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.entity.Skill;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileDTO {
    private String name;
    private String newPassword;
    private String contact;
    private MultipartFile profileImage;

    private String companyName;
    private String experience;
    private Boolean available;
    private List<SkillRequestDTO> skills;
}
