package com.lowes.mapper;

import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class UserConverter {
    private final PasswordEncoder passwordEncoder;

    public User authRegisterDTOtoUser(AuthRegisterDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole().toUpperCase()))
                .contact(dto.getContact())
                //.pic(dto.getPic() != null ? dto.getPic().getOriginalFilename() : null)
                .build();
    }

    public Vendor authRegisterDTOtoVendor(AuthRegisterDTO dto, User user, List<Skill> skills) {
        return Vendor.builder()
                .companyName(dto.getCompanyName())
                .experience(dto.getExperience())
                .available(dto.getAvailable())
                .approved(null)
                .skills(skills)
                .user(user)
                .build();
    }
}
