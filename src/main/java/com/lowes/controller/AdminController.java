package com.lowes.controller;

import com.lowes.dto.request.admin.MaterialRequestAdminDTO;

import com.lowes.entity.enums.PhaseType;
import com.lowes.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    //CUSTOMER
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam int page, @RequestParam int size) {
        return adminService.getAllCustomers(page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        return adminService.deleteUser(id);
    }

    //VENDOR
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/vendors/approved")
    public ResponseEntity<?> getApprovedVendors(@RequestParam int page, @RequestParam int size) {
        return adminService.getApprovedVendors(page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/vendors/pending")
    public ResponseEntity<?> getApprovalPendingVendors(@RequestParam int page, @RequestParam int size) {
        return adminService.getApprovalPendingVendors(page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/vendor/{id}/approve")
    public ResponseEntity<?> updateVendorApproval(@PathVariable UUID id, @RequestParam boolean approved) {
        return adminService.updateVendorApproval(id, approved);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/vendor/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable UUID id) {
        return adminService.deleteVendor(id);
    }

    //MATERIALS
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/materials")
    public ResponseEntity<?> getAllMaterials(@RequestParam(name = "phaseType", required = false) PhaseType phaseType, @RequestParam(name = "deleted", required = false) Boolean deleted, @RequestParam int page, @RequestParam int size){
        return adminService.getAllMaterials(phaseType, deleted, page, size);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/materials/{id}")
    public ResponseEntity<?> getMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.getMaterialByExposedId(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/materials")
    public ResponseEntity<?> addMaterial(@RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        return adminService.addMaterial(materialRequestAdminDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/materials/{id}")
    public ResponseEntity<?> updateMaterialByExposedId(@PathVariable("id") UUID id, @RequestBody MaterialRequestAdminDTO materialRequestAdminDTO){
        return adminService.updateMaterialByExposedId(id, materialRequestAdminDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/materials/delete/{id}")
    public ResponseEntity<?> deleteMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.deleteMaterialByExposedId(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/materials/re-add/{id}")
    public ResponseEntity<?> reAddMaterialByExposedId(@PathVariable("id") UUID id){
        return adminService.reAddMaterialByExposedId(id);
    }
}
