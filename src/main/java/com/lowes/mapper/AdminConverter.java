package com.lowes.mapper;

import com.lowes.dto.request.MaterialRequestAdminDTO;
import com.lowes.dto.response.MaterialResponseAdminDTO;
import com.lowes.dto.response.UserResponseAdminDTO;
import com.lowes.dto.response.VendorResponseAdminDTO;
import com.lowes.entity.Material;
import com.lowes.entity.User;
import com.lowes.entity.Vendor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class AdminConverter {

    public UserResponseAdminDTO usertoUserResponseAdminDTO(User user) {
        return UserResponseAdminDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contact(user.getContact())
                .pic(user.getPic())
                .build();
    }


    public VendorResponseAdminDTO vendorToVendorResponseAdminDTO(Vendor vendor) {
        return VendorResponseAdminDTO.builder()
                .id(vendor.getId())
                .name(vendor.getUser().getName())
                .email(vendor.getUser().getEmail())
                .contact(vendor.getUser().getContact())
                .pic(vendor.getUser().getPic())
                .companyName(vendor.getCompanyName())
                .available(vendor.getAvailable())
                .approved(vendor.getApproved())
                .experience(vendor.getExperience())
                .skills(formatSkills(vendor))
                .build();
    }

    private List<String> formatSkills(Vendor vendor) {
        return vendor.getSkills().stream()
                .map(skill -> {
                    String skillName = skill.getName().name();
                    Double basePrice = skill.getBasePrice();
                    return skillName + " - ₹" + basePrice;
                })
                .collect(Collectors.toList());
    }

    public static MaterialResponseAdminDTO materialToMaterialAdminResponse(Material material){
        MaterialResponseAdminDTO materialResponseAdminDTO =  MaterialResponseAdminDTO.builder()
                .exposedId(material.getExposedId())
                .name(material.getName())
                .unit(material.getUnit())
                .phaseType(material.getPhaseType())
                .pricePerQuantity(material.getPricePerQuantity())
                .deleted(material.isDeleted())
                .build();


        return materialResponseAdminDTO;
    }

    public static Material materialAdminRequestToMaterial(MaterialRequestAdminDTO materialRequestAdminDTO){
        Material material = Material.builder()
                .name(materialRequestAdminDTO.getName())
                .unit(materialRequestAdminDTO.getUnit())
                .phaseType(materialRequestAdminDTO.getPhaseType())
                .pricePerQuantity(materialRequestAdminDTO.getPricePerQuantity())
                .exposedId(UUID.randomUUID())
                .build();

        return  material;
    }

}
