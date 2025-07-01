package com.lowes.service;

import com.lowes.dto.request.admin.MaterialRequestAdminDTO;
import com.lowes.dto.response.admin.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    public ResponseEntity<?> getAllCustomers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> customerPage = userRepository.findByRole(Role.CUSTOMER, pageable);

            List<UserResponseAdminDTO> customerDTOs = customerPage.stream()
                    .map(adminConverter::usertoUserResponseAdminDTO)
                    .toList();

            AdminPaginatedResponseDTO<UserResponseAdminDTO> response = AdminPaginatedResponseDTO.<UserResponseAdminDTO>builder()
                    .content(customerDTOs)
                    .pageNumber(page)
                    .pageSize(size)
                    .totalElements(customerPage.getTotalElements())
                    .totalPages(customerPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteUser(UUID id) {
        try {
            User user = userRepository.findByExposedId(id);
            if (user == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AdminToastDTO.builder().message("ERROR").build());
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

    public ResponseEntity<?> getApprovedVendors(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Vendor> vendorsPage = vendorRepository.findByApproved(true, pageable);

            List<VendorResponseAdminDTO> dtos = vendorsPage
                    .stream()
                    .map(adminConverter::vendorToVendorResponseAdminDTO)
                    .toList();

            AdminPaginatedResponseDTO<VendorResponseAdminDTO> response = AdminPaginatedResponseDTO.<VendorResponseAdminDTO>builder()
                    .content(dtos)
                    .totalPages(vendorsPage.getTotalPages())
                    .totalElements(vendorsPage.getTotalElements())
                    .pageNumber(vendorsPage.getNumber())
                    .pageSize(vendorsPage.getSize())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception Occurred: " + e);
        }
    }

    public ResponseEntity<?> getApprovalPendingVendors(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Vendor> vendorsPage = vendorRepository.findByApprovedIsNull(pageable);

            List<VendorResponseAdminDTO> dtos = vendorsPage
                    .stream()
                    .map(adminConverter::vendorToVendorResponseAdminDTO)
                    .toList();

            AdminPaginatedResponseDTO<VendorResponseAdminDTO> response = AdminPaginatedResponseDTO.<VendorResponseAdminDTO>builder()
                    .content(dtos)
                    .totalPages(vendorsPage.getTotalPages())
                    .totalElements(vendorsPage.getTotalElements())
                    .pageNumber(vendorsPage.getNumber())
                    .pageSize(vendorsPage.getSize())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception Occurred: " + e);
        }
    }


    @Transactional
    public ResponseEntity<?> updateVendorApproval(UUID id, boolean approved) {
        try {
            System.out.println(id);
            Vendor vendor = vendorRepository.findByExposedId(id);
            System.out.println(vendor);
            if (vendor == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AdminToastDTO.builder().message("ERROR").build());
            if (approved) {
                vendor.setApproved(true);
                vendorRepository.save(vendor);
                return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
            } else {
                return this.deleteVendor(id);
            }
        } catch (Exception e) {
            System.out.println(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AdminToastDTO.builder().message("ERROR").build());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteVendor(UUID vendorId) {
        try {
            Vendor vendor = vendorRepository.findByExposedId(vendorId);

            if (vendor == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AdminToastDTO.builder().message("ERROR").build());

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
    public ResponseEntity<?> getAllMaterials(PhaseType phaseType, Boolean deleted, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "deleted", "phaseType", "id"));
            Page<Material> materialPage;

            if (phaseType != null && deleted != null) {
                materialPage = materialRepository.findByPhaseTypeAndDeleted(phaseType, deleted, pageable);
            } else if (phaseType != null) {
                materialPage = materialRepository.findByPhaseType(phaseType, pageable);
            } else if (deleted != null) {
                materialPage = materialRepository.findByDeleted(deleted, pageable);
            } else {
                materialPage = materialRepository.findAll(pageable);
            }

            List<MaterialResponseAdminDTO> materialDTOs = materialPage.stream()
                    .map(AdminConverter::materialToMaterialAdminResponse)
                    .toList();

            AdminPaginatedResponseDTO<MaterialResponseAdminDTO> response = AdminPaginatedResponseDTO.<MaterialResponseAdminDTO>builder()
                    .content(materialDTOs)
                    .pageNumber(page)
                    .pageSize(size)
                    .totalElements(materialPage.getTotalElements())
                    .totalPages(materialPage.getTotalPages())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AdminToastDTO.builder().message("ERROR").build());
        }
    }


    public ResponseEntity<?> getMaterialByExposedId(UUID id) {
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty()) {
                throw new ElementNotFoundException("Material Not Found To Update");
            }
            Material material = optionalMaterial.get();
            return ResponseEntity.ok(AdminConverter.materialToMaterialAdminResponse(material));
        } catch (ElementNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : " + exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> addMaterial(MaterialRequestAdminDTO materialRequestAdminDTO) {
        try {
            Material material = AdminConverter.materialAdminRequestToMaterial(materialRequestAdminDTO);
            materialRepository.save(material);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (DataIntegrityViolationException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: " + exception.getMessage());
        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : " + exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> updateMaterialByExposedId(UUID id, MaterialRequestAdminDTO materialRequestAdminDTO) {
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

            materialRepository.save(existingMaterial);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (ElementNotFoundException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (DataIntegrityViolationException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data Integrity Violation: " + exception.getMessage());
        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : " + exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> deleteMaterialByExposedId(UUID id) {
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
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (ElementNotFoundException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (OperationNotAllowedException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : " + exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> reAddMaterialByExposedId(UUID id) {
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
            materialRepository.save(material);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (ElementNotFoundException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (OperationNotAllowedException exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (Exception exception) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error : " + exception.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> hardDeleteMaterialByExposedId(UUID id) {
        try {
            Optional<Material> optionalMaterial = materialRepository.findByExposedId(id);
            if (optionalMaterial.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Material not found");
            Material material = optionalMaterial.get();
            materialRepository.delete(material);
            return ResponseEntity.ok(AdminToastDTO.builder().message("SUCCESS").build());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error hard deleting material");
        }
    }
}