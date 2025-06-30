package com.lowes.service;


import com.lowes.convertor.MaterialConvertor;
import com.lowes.dto.request.MaterialRequestAdminDTO;
import com.lowes.dto.response.MaterialResponseAdminDTO;
import com.lowes.dto.response.UserResponseAdminDTO;
import com.lowes.dto.response.VendorResponseAdminDTO;
import com.lowes.entity.*;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Role;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.exception.OperationNotAllowedException;
import com.lowes.mapper.AdminConverter;
import com.lowes.repository.MaterialRepository;
import com.lowes.repository.SkillRepository;
import com.lowes.repository.UserRepository;
import com.lowes.repository.VendorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AdminConverter adminConverter;
    private final VendorRepository vendorRepository;
    private final SkillRepository skillRepository;
    private final MaterialRepository materialRepository;

    public List<UserResponseAdminDTO> getAllCustomers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .map(adminConverter::usertoUserResponseAdminDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProjects() != null) {
            for (Project project : user.getProjects()) {
                project.setOwner(null);
            }
            user.getProjects().clear();
        }

        if (user.getVendorsServingThisUser() != null) {
            user.getVendorsServingThisUser().forEach(vendor -> vendor.getCustomers().remove(user));
            user.getVendorsServingThisUser().clear();
        }

        if (user.getVendorReviews() != null) user.getVendorReviews().clear();

        userRepository.delete(user);
    }


    public List<VendorResponseAdminDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(adminConverter::vendorToVendorResponseAdminDTO)
                .collect(Collectors.toList());
    }

    public void updateVendorApproval(UUID id, boolean approved) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setApproved(approved);
        System.out.println(approved);
        System.out.println(vendor.toString());
        vendorRepository.save(vendor);
    }

    @Transactional
    public void deleteVendor(UUID vendorId) {
        Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);

        Vendor vendor = vendorOpt.get();
                //.orElseThrow(() -> new VendorNotFoundException("Vendor not found"));

        // STEP 1: Unlink vendor from skills
        // updates the vendor_skills table as well
        if (vendor.getSkills() != null) {
            for (Skill skill : vendor.getSkills()) {
                skill.getVendors().remove(vendor);
            }
            vendor.getSkills().clear();
        }

        // STEP 2: Delete orphan skills (skills with no vendors)
        List<Skill> orphanSkills = skillRepository.findSkillsWithNoVendors();
        skillRepository.deleteAll(orphanSkills);

        // STEP 3: Unlink vendor from customers
        if (vendor.getCustomers() != null) {
            for (User customer : vendor.getCustomers()) {
                customer.getVendorsServingThisUser().remove(vendor);
            }
            vendor.getCustomers().clear();
        }

        // STEP 4: Delete vendor
        vendorRepository.delete(vendor);
    }



    //MATERIALS
    public List<MaterialResponseAdminDTO> getAllMaterials(PhaseType phaseType, Boolean deleted){

        List<Material> materialList;
        if(phaseType !=null && deleted!=null){
            materialList = materialRepository.findByPhaseTypeAndDeleted(phaseType,deleted, Sort.by(Sort.Direction.ASC,"id"));

        }
        else if(phaseType !=null){
            materialList = materialRepository.findByPhaseType(phaseType, Sort.by(Sort.Direction.ASC,"deleted","id"));
        }
        else if(deleted!=null){
            materialList = materialRepository.findByDeleted(deleted, Sort.by(Sort.Direction.ASC,"phaseType","id"));
        }
        else{
            materialList = materialRepository.findAll(Sort.by(Sort.Direction.ASC,"deleted","phaseType","id"));
        }
        List<MaterialResponseAdminDTO> materialResponseAdminDTOList = new ArrayList<>();
        if(!materialList.isEmpty()){
            for(Material material : materialList){
                materialResponseAdminDTOList.add(AdminConverter.materialToMaterialAdminResponse(material));
            }
        }
        return materialResponseAdminDTOList;
    }

    public MaterialResponseAdminDTO getMaterialByExposedId(UUID id){
        Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material material = optionalMaterial.get();
        MaterialResponseAdminDTO materialResponseAdminDTO = AdminConverter.materialToMaterialAdminResponse(material);
        return materialResponseAdminDTO;
    }




    @Transactional
    public MaterialResponseAdminDTO addMaterial(MaterialRequestAdminDTO materialRequestAdminDTO){
        Material material = AdminConverter.materialAdminRequestToMaterial(materialRequestAdminDTO);
        Material savedMaterial = materialRepository.save(material);
        MaterialResponseAdminDTO materialResponseAdminDTO = AdminConverter.materialToMaterialAdminResponse(savedMaterial);
        return materialResponseAdminDTO;
    }

    @Transactional
    public MaterialResponseAdminDTO updateMaterialByExposedId(UUID id, MaterialRequestAdminDTO materialRequestAdminDTO){
        Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Update");
        }
        Material existingMaterial = optionalMaterial.get();

        existingMaterial.setName(materialRequestAdminDTO.getName());
        existingMaterial.setUnit(materialRequestAdminDTO.getUnit());
        existingMaterial.setPhaseType(materialRequestAdminDTO.getPhaseType());
        existingMaterial.setPricePerQuantity(materialRequestAdminDTO.getPricePerQuantity());

        Material updatedMaterial = materialRepository.save(existingMaterial);
        MaterialResponseAdminDTO materialResponseAdminDTO = AdminConverter.materialToMaterialAdminResponse(updatedMaterial);
        return materialResponseAdminDTO;
    }

    @Transactional
    public MaterialResponseAdminDTO deleteMaterialByExposedId(UUID id){
        Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
        if(optionalMaterial.isEmpty()){
            throw new ElementNotFoundException("Material Not Found To Delete");
        }
        Material material = optionalMaterial.get();

        if(material.isDeleted()){
            throw new OperationNotAllowedException("Cannot Delete A Material That Is Already Deleted");
        }
        material.setDeleted(true);
        Material savedMaterial = materialRepository.save(material);
        MaterialResponseAdminDTO materialResponseAdminDTO = AdminConverter.materialToMaterialAdminResponse(savedMaterial);
        return materialResponseAdminDTO;
    }

    @Transactional
    public MaterialResponseAdminDTO reAddMaterialByExposedId(UUID id){
        Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
        if(optionalMaterial.isEmpty()){
            throw  new ElementNotFoundException("Material Not Found To Re-Add");
        }
        Material material = optionalMaterial.get();

        if(!material.isDeleted()){
            throw new OperationNotAllowedException("Cannot Re Add A Material That Is Not Deleted");
        }

        material.setDeleted(false);
        Material savedMaterial = materialRepository.save(material);

        MaterialResponseAdminDTO materialResponseAdminDTO = AdminConverter.materialToMaterialAdminResponse(savedMaterial);
        return materialResponseAdminDTO;
    }



}
