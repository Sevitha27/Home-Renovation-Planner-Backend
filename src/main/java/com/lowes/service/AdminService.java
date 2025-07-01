package com.lowes.service;

import com.lowes.dto.request.MaterialRequestAdminDTO;
import com.lowes.dto.response.AdminToastDTO;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final SkillRepository skillRepository;
    private final MaterialRepository materialRepository;

    private final AdminConverter adminConverter;

    public ResponseEntity<?> getAllCustomers() {
        try {
            List<UserResponseAdminDTO> list = userRepository.findAll()
                    .stream()
                    .filter(user -> user.getRole() == Role.CUSTOMER)
                    .map(adminConverter::usertoUserResponseAdminDTO)
                    .toList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteUser(UUID id) {
        try {
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
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }
    }


    public ResponseEntity<?> getAllVendors() {
        try {
            List<VendorResponseAdminDTO> list =  vendorRepository.findAll().stream()
                    .map(adminConverter::vendorToVendorResponseAdminDTO)
                    .toList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception Occurred : " + e);
        }
    }

    public ResponseEntity<?> updateVendorApproval(UUID id, boolean approved) {
        try {
            Vendor vendor = vendorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            vendor.setApproved(approved);
            vendorRepository.save(vendor);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteVendor(UUID vendorId) {
        try {
            Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);

            if(vendorOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AdminToastDTO.builder().message("ERROR").build());
            Vendor vendor = vendorOpt.get();

            if (vendor.getSkills() != null) {
                for (Skill skill : vendor.getSkills()) {
                    skill.getVendors().remove(vendor);
                }
                vendor.getSkills().clear();
            }

            List<Skill> orphanSkills = skillRepository.findSkillsWithNoVendors();
            skillRepository.deleteAll(orphanSkills);

            if (vendor.getCustomers() != null) {
                for (User customer : vendor.getCustomers()) {
                    customer.getVendorsServingThisUser().remove(vendor);
                }
                vendor.getCustomers().clear();
            }

            vendorRepository.delete(vendor);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }
    }


    //MATERIALS
    public ResponseEntity<?> getAllMaterials(PhaseType phaseType, Boolean deleted){
        try {
            List<Material> materialList;
            if (phaseType != null && deleted != null) {
                materialList = materialRepository.findByPhaseTypeAndDeleted(phaseType, deleted, Sort.by(Sort.Direction.ASC, "id"));

            } else if (phaseType != null) {
                materialList = materialRepository.findByPhaseType(phaseType, Sort.by(Sort.Direction.ASC, "deleted", "id"));
            } else if (deleted != null) {
                materialList = materialRepository.findByDeleted(deleted, Sort.by(Sort.Direction.ASC, "phaseType", "id"));
            } else {
                materialList = materialRepository.findAll(Sort.by(Sort.Direction.ASC, "deleted", "phaseType", "id"));
            }
            List<MaterialResponseAdminDTO> materialResponseAdminDTOList = new ArrayList<>();
            if (!materialList.isEmpty()) {
                for (Material material : materialList) {
                    materialResponseAdminDTOList.add(AdminConverter.materialToMaterialAdminResponse(material));
                }
            }
            return ResponseEntity.ok(materialResponseAdminDTOList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }
    }

    public ResponseEntity<?> getMaterialByExposedId(UUID id){
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty()) {
                throw new ElementNotFoundException("Material Not Found To Update");
            }
            Material material = optionalMaterial.get();
            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(material));
        } catch (ElementNotFoundException exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> addMaterial(MaterialRequestAdminDTO materialRequestAdminDTO){
        try {
            Material material = AdminConverter.materialAdminRequestToMaterial(materialRequestAdminDTO);
            Material savedMaterial = materialRepository.save(material);
            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(savedMaterial));
        }catch(DataIntegrityViolationException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: "+exception.getMessage());
        }
        catch(Exception exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> updateMaterialByExposedId(UUID id, MaterialRequestAdminDTO materialRequestAdminDTO){
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty()) {
                throw new ElementNotFoundException("Material Not Found To Update");
            }
            Material existingMaterial = optionalMaterial.get();

            existingMaterial.setName(materialRequestAdminDTO.getName());
            existingMaterial.setUnit(materialRequestAdminDTO.getUnit());
            existingMaterial.setPhaseType(materialRequestAdminDTO.getPhaseType());
            existingMaterial.setPricePerQuantity(materialRequestAdminDTO.getPricePerQuantity());

            Material updatedMaterial = materialRepository.save(existingMaterial);
            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(updatedMaterial));
        } catch (ElementNotFoundException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (DataIntegrityViolationException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: "+exception.getMessage());
        } catch (Exception exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteMaterialByExposedId(UUID id){
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty()) {
                throw new ElementNotFoundException("Material Not Found To Delete");
            }
            Material material = optionalMaterial.get();

            if (material.isDeleted()) {
                throw new OperationNotAllowedException("Cannot Delete A Material That Is Already Deleted");
            }
            material.setDeleted(true);
            Material savedMaterial = materialRepository.save(material);
            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(savedMaterial));
        }catch (ElementNotFoundException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(OperationNotAllowedException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch(Exception exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> reAddMaterialByExposedId(UUID id){
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty()) {
                throw new ElementNotFoundException("Material Not Found To Re-Add");
            }
            Material material = optionalMaterial.get();

            if (!material.isDeleted()) {
                throw new OperationNotAllowedException("Cannot Re Add A Material That Is Not Deleted");
            }

            material.setDeleted(false);
            Material savedMaterial = materialRepository.save(material);

            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(savedMaterial));
        }catch (ElementNotFoundException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        catch(OperationNotAllowedException exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        catch(Exception exception){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : "+exception.getMessage());
        }
    }
}