package com.lowes.service;

import com.lowes.dto.request.AuthRegisterDTO;
import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.SkillType;
import com.lowes.mapper.UserConverter;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final UserConverter userConverter;
    private final SkillRepository skillRepository;


    @Transactional
    public ResponseEntity<String> register(AuthRegisterDTO request) {
        User user = userConverter.authRegisterDTOtoUser(request);
        userRepository.save(user);

        if (user.getRole() == Role.VENDOR) {
            List<Skill> skills = new ArrayList<>();
            for (SkillRequestDTO skillDTO : request.getSkills()) {
                SkillType skillType = SkillType.valueOf(skillDTO.getSkillName().toUpperCase());
                Skill skill = skillRepository.findByNameAndBasePrice(String.valueOf(skillType),skillDTO.getBasePrice())
                        .orElseGet(() -> skillRepository.save(
                                Skill.builder()
                                        .name(skillType)
                                        .basePrice(skillDTO.getBasePrice())
                                        .build()
                        ));
                skills.add(skill);
            }

            Vendor vendor = userConverter.authRegisterDTOtoVendor(request, user, skills);
            vendorRepository.save(vendor);
        }

        return ResponseEntity.ok("User registered successfully.");
    }

}