package com.lowes.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegisterDTO {

    //user
    @NonNull
    private String name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String role;

    @NonNull
    private String contact;



    //additional for vendor
    @NonNull
    private String companyName;

    @NonNull
    private String experience;

    @NonNull
    private Boolean available;

    @NonNull
    private List<SkillRequestDTO> skills;

}