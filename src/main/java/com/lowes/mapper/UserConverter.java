package com.lowes.mapper;

import com.lowes.dto.request.SkillRequestDTO;
import com.lowes.dto.request.auth.AuthRegisterDTO;
import com.lowes.dto.request.auth.UpdateUserProfileDTO;
import com.lowes.dto.response.auth.GetCustomerProfileDTO;
import com.lowes.dto.response.auth.GetVendorProfileDTO;
import com.lowes.entity.Skill;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.Role;
import com.lowes.entity.enums.SkillType;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class UserConverter {
    private final PasswordEncoder passwordEncoder;
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public User authRegisterDTOtoUser(AuthRegisterDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.valueOf(dto.getRole().toUpperCase()))
                .contact(dto.getContact())
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

//    @Transactional
//    public void updateUserProfileDTOToUser(UpdateUserProfileDTO dto, User user, String url) {
//        if (user.getRole() == Role.VENDOR) {
//            Vendor vendor = vendorRepository.findByUser(user);
//            vendor.setCompanyName(dto.getCompanyName());
//            vendor.setExperience(dto.getExperience());
//            vendor.setAvailable(dto.getAvailable());
//
//            List<Skill> updatedSkillList = new ArrayList<>();
//            List<Skill> oldSkillsToCheck = new ArrayList<>();
//
//            for (SkillRequestDTO skillDto : dto.getSkills()) {
//                SkillType skillName = SkillType.valueOf(skillDto.getSkillName());
//                Double newBasePrice = skillDto.getBasePrice();
//
//                Skill existingSkillInVendor = vendor.getSkills().stream()
//                        .filter(s -> s.getName() == skillName)
//                        .findFirst()
//                        .orElse(null);
//
//                if (existingSkillInVendor != null) {
//                    vendor.getSkills().remove(existingSkillInVendor);
//                    oldSkillsToCheck.add(existingSkillInVendor);
//                }
//
//                Skill skillToAdd = skillRepository.findByNameAndBasePrice(skillName, newBasePrice)
//                        .orElse(null);
//
//                if (skillToAdd == null) {
//                    skillToAdd = new Skill();
//                    skillToAdd.setName(skillName);
//                    skillToAdd.setBasePrice(newBasePrice);
//                    skillToAdd = skillRepository.save(skillToAdd);
//                }
//
//                updatedSkillList.add(skillToAdd);
//            }
//
//            vendor.getSkills().addAll(updatedSkillList);
//            vendorRepository.save(vendor);
//
//            for (Skill oldSkill : oldSkillsToCheck) {
//                long count = vendorRepository.countBySkillsContaining(oldSkill);
//                if (count == 0) {
//                    skillRepository.delete(oldSkill);
//                }
//            }
//        }
//
//        user.setName(dto.getName());
//        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
//        user.setContact(dto.getContact());
//        user.setPic(url);
//        userRepository.save(user);
//    }

    @Transactional
    public void updateUserProfileDTOToUser(UpdateUserProfileDTO dto, User user, String url) {
        if (user.getRole() == Role.VENDOR) {
            Vendor vendor = vendorRepository.findByUser(user);

            // Update only non-null fields
            if (dto.getCompanyName() != null) {
                vendor.setCompanyName(dto.getCompanyName());
            }

            if (dto.getExperience() != null) {
                vendor.setExperience(dto.getExperience());
            }

            if (dto.getAvailable() != null) {
                vendor.setAvailable(dto.getAvailable());
            }

            // Only update skills if they are provided
            if (dto.getSkills() != null && !dto.getSkills().isEmpty()) {
                List<Skill> updatedSkillList = new ArrayList<>();
                List<Skill> oldSkillsToCheck = new ArrayList<>();

                for (SkillRequestDTO skillDto : dto.getSkills()) {
                    SkillType skillName = SkillType.valueOf(skillDto.getSkillName());
                    Double newBasePrice = skillDto.getBasePrice();

                    Skill existingSkillInVendor = vendor.getSkills().stream()
                            .filter(s -> s.getName() == skillName)
                            .findFirst()
                            .orElse(null);

                    if (existingSkillInVendor != null) {
                        vendor.getSkills().remove(existingSkillInVendor);
                        oldSkillsToCheck.add(existingSkillInVendor);
                    }

                    Skill skillToAdd = skillRepository.findByNameAndBasePrice(skillName, newBasePrice)
                            .orElse(null);

                    if (skillToAdd == null) {
                        skillToAdd = new Skill();
                        skillToAdd.setName(skillName);
                        skillToAdd.setBasePrice(newBasePrice);
                        skillToAdd = skillRepository.save(skillToAdd);
                    }

                    updatedSkillList.add(skillToAdd);
                }

                vendor.getSkills().addAll(updatedSkillList);
                vendorRepository.save(vendor);

                for (Skill oldSkill : oldSkillsToCheck) {
                    long count = vendorRepository.countBySkillsContaining(oldSkill);
                    if (count == 0) {
                        skillRepository.delete(oldSkill);
                    }
                }
            }
        }

        System.out.println(dto.getName());
        System.out.println(dto.getContact());

        // Only update user fields if non-null
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getNewPassword() != null && !dto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.getContact() != null) {
            user.setContact(dto.getContact());
        }

        if (url != null) {
            user.setPic(url);
        }

        userRepository.save(user);
    }


    public GetCustomerProfileDTO userToGetCustomerProfileDTO(User user){
        return GetCustomerProfileDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .contact(user.getContact())
                .url(user.getPic())
                .build();

    }

    public GetVendorProfileDTO userToGetVendorProfileDTO(User user){
        Vendor vendor= vendorRepository.findByUser(user);
        List<SkillRequestDTO> list= new ArrayList<>();
        for (Skill skill : vendor.getSkills()){
            list.add(SkillRequestDTO.builder().
                    skillName(String.valueOf(skill.getName())).
                    basePrice(skill.getBasePrice())
                    .build());
        }

        return GetVendorProfileDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .contact(user.getContact())
                .url(user.getPic())
                .skills(list)
                .companyName(vendor.getCompanyName())
                .experience(vendor.getExperience())
                .available(vendor.getAvailable())
                .build();
    }

}
